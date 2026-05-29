package com.zyc4fun.majiangscoreapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyc4fun.majiangscoreapi.common.BusinessException;
import com.zyc4fun.majiangscoreapi.dto.PlayerDto;
import com.zyc4fun.majiangscoreapi.dto.RoomDtos;
import com.zyc4fun.majiangscoreapi.dto.ScoreHistoryDtos;
import com.zyc4fun.majiangscoreapi.dto.ScoreRecordDto;
import com.zyc4fun.majiangscoreapi.entity.AppUser;
import com.zyc4fun.majiangscoreapi.entity.ScoreRecord;
import com.zyc4fun.majiangscoreapi.entity.ScoreRecordDetail;
import com.zyc4fun.majiangscoreapi.repository.AppUserRepository;
import com.zyc4fun.majiangscoreapi.repository.ScoreRecordDetailRepository;
import com.zyc4fun.majiangscoreapi.repository.ScoreRecordRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class RoomService {
    private static final String ROOM_PREFIX = "majiang:room:";

    private final AppUserRepository appUserRepository;
    private final ScoreRecordRepository scoreRecordRepository;
    private final ScoreRecordDetailRepository scoreRecordDetailRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    public RoomService(AppUserRepository appUserRepository, ScoreRecordRepository scoreRecordRepository,
                       ScoreRecordDetailRepository scoreRecordDetailRepository, StringRedisTemplate redisTemplate,
                       ObjectMapper objectMapper) {
        this.appUserRepository = appUserRepository;
        this.scoreRecordRepository = scoreRecordRepository;
        this.scoreRecordDetailRepository = scoreRecordDetailRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public RoomDtos.RoomDto createRoom(AppUser user) {
        String roomCode = generateRoomCode();
        redisTemplate.opsForHash().put(metaKey(roomCode), "ownerUserId", String.valueOf(user.getId()));
        redisTemplate.opsForHash().put(metaKey(roomCode), "ownerName", user.getNickname());
        redisTemplate.opsForHash().put(metaKey(roomCode), "createdAt", LocalDateTime.now().toString());
        addPlayer(roomCode, user);
        return detail(roomCode, user);
    }

    public RoomDtos.RoomDto joinRoom(AppUser user, String roomCode) {
        if (!StringUtils.hasText(roomCode)) {
            throw new BusinessException("请输入房间号");
        }
        ensureRoomExists(roomCode);
        addPlayer(roomCode, user);
        return detail(roomCode, user);
    }

    public RoomDtos.RoomDto detail(String roomCode, AppUser user) {
        ensureRoomExists(roomCode);
        List<PlayerDto> players = loadPlayers(roomCode);
        Long currentPlayerId = containsPlayer(players, user.getId()) ? user.getId() : null;
        return new RoomDtos.RoomDto(
                roomCode,
                Long.valueOf(String.valueOf(redisTemplate.opsForHash().get(metaKey(roomCode), "ownerUserId"))),
                String.valueOf(redisTemplate.opsForHash().get(metaKey(roomCode), "ownerName")),
                currentPlayerId,
                LocalDateTime.parse(String.valueOf(redisTemplate.opsForHash().get(metaKey(roomCode), "createdAt"))),
                players,
                loadRecords(roomCode)
        );
    }

    public RoomDtos.RoomDto spend(AppUser user, String roomCode, Long playerId, int amount) {
        if (amount <= 0) {
            throw new BusinessException("支出积分必须大于 0");
        }
        ensureRoomExists(roomCode);
        ensureInRoom(roomCode, user.getId());
        if (user.getId().equals(playerId)) {
            throw new BusinessException("自己不可支出");
        }

        applySpend(roomCode, playerId, amount);
        return detail(roomCode, user);
    }

    public RoomDtos.RoomDto batchSpend(AppUser user, String roomCode, Map<Long, Integer> spends) {
        if (CollectionUtils.isEmpty(spends)) {
            throw new BusinessException("请输入支出积分");
        }
        ensureRoomExists(roomCode);
        ensureInRoom(roomCode, user.getId());

        for (Map.Entry<Long, Integer> entry : spends.entrySet()) {
            Integer amount = entry.getValue();
            if (amount == null || amount <= 0 || user.getId().equals(entry.getKey())) {
                continue;
            }
            applySpend(roomCode, entry.getKey(), amount);
        }

        return detail(roomCode, user);
    }

    @Transactional
    public ScoreHistoryDtos.ScoreHistoryDto settle(AppUser user, String roomCode) {
        ensureRoomExists(roomCode);
        ensureInRoom(roomCode, user.getId());

        List<PlayerDto> players = loadPlayers(roomCode);
        if (players.isEmpty()) {
            throw new BusinessException("房间内暂无玩家");
        }

        List<ScoreHistoryDtos.ScoreHistoryPlayerDto> historyPlayers = new ArrayList<ScoreHistoryDtos.ScoreHistoryPlayerDto>();
        int max = players.get(0).getScore();
        int min = players.get(0).getScore();
        for (PlayerDto player : players) {
            max = Math.max(max, player.getScore());
            min = Math.min(min, player.getScore());
            historyPlayers.add(new ScoreHistoryDtos.ScoreHistoryPlayerDto(player.getName(), player.getScore()));
        }

        LocalDateTime createdAt = LocalDateTime.now();
        ScoreRecordDetail detail = scoreRecordDetailRepository.save(new ScoreRecordDetail(0L, writeJson(historyPlayers)));
        List<ScoreRecord> records = new ArrayList<ScoreRecord>();
        boolean hasWinnerAndLoser = max > min;
        for (PlayerDto player : players) {
            records.add(new ScoreRecord(player.getUserId(), detail.getId(), createdAt));
            AppUser appUser = appUserRepository.findById(player.getUserId()).orElse(null);
            if (appUser != null) {
                appUser.recordSettlementResult(
                        player.getScore(),
                        hasWinnerAndLoser && player.getScore() == max,
                        hasWinnerAndLoser && player.getScore() == min
                );
                appUserRepository.save(appUser);
            }
        }
        records = scoreRecordRepository.saveAll(records);
        if (!records.isEmpty()) {
            detail.setRecordId(records.get(0).getId());
            scoreRecordDetailRepository.save(detail);
        }

        clearRoomRedis(roomCode);
        return new ScoreHistoryDtos.ScoreHistoryDto(records.isEmpty() ? null : records.get(0).getId(), createdAt, historyPlayers);
    }

    private void addPlayer(String roomCode, AppUser user) {
        String userId = String.valueOf(user.getId());
        if (Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(playersKey(roomCode), userId))) {
            return;
        }

        PlayerDto player = new PlayerDto(user.getId(), user.getId(), user.getNickname(), user.getAvatarUrl(), 0, 0, 0);
        redisTemplate.opsForHash().put(playersKey(roomCode), userId, writeJson(player));
        redisTemplate.opsForList().rightPush(playerOrderKey(roomCode), userId);
    }

    private void applySpend(String roomCode, Long playerId, int amount) {
        PlayerDto player = readPlayer(roomCode, playerId);
        PlayerDto updated = new PlayerDto(
                player.getId(),
                player.getUserId(),
                player.getName(),
                player.getAvatarUrl(),
                player.getWin(),
                player.getLose(),
                player.getScore() - amount
        );
        redisTemplate.opsForHash().put(playersKey(roomCode), String.valueOf(playerId), writeJson(updated));
        ScoreRecordDto record = new ScoreRecordDto(
                System.currentTimeMillis(),
                roomCode,
                playerId,
                player.getName(),
                -amount,
                LocalDateTime.now()
        );
        redisTemplate.opsForList().leftPush(recordsKey(roomCode), writeJson(record));
        redisTemplate.opsForList().trim(recordsKey(roomCode), 0, 49);
    }

    private void ensureRoomExists(String roomCode) {
        if (!StringUtils.hasText(roomCode) || !Boolean.TRUE.equals(redisTemplate.hasKey(metaKey(roomCode)))) {
            throw new BusinessException("房间号不存在");
        }
    }

    private void ensureInRoom(String roomCode, Long userId) {
        if (!Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(playersKey(roomCode), String.valueOf(userId)))) {
            throw new BusinessException("你不在该房间");
        }
    }

    private PlayerDto readPlayer(String roomCode, Long userId) {
        Object value = redisTemplate.opsForHash().get(playersKey(roomCode), String.valueOf(userId));
        if (value == null) {
            throw new BusinessException("玩家不存在");
        }
        return readJson(String.valueOf(value), PlayerDto.class);
    }

    private List<PlayerDto> loadPlayers(String roomCode) {
        List<String> ids = redisTemplate.opsForList().range(playerOrderKey(roomCode), 0, -1);
        if (ids == null) {
            return Collections.emptyList();
        }

        List<PlayerDto> players = new ArrayList<PlayerDto>();
        for (String id : ids) {
            Object value = redisTemplate.opsForHash().get(playersKey(roomCode), id);
            if (value != null) {
                players.add(readJson(String.valueOf(value), PlayerDto.class));
            }
        }
        return players;
    }

    private List<ScoreRecordDto> loadRecords(String roomCode) {
        List<String> values = redisTemplate.opsForList().range(recordsKey(roomCode), 0, 49);
        if (values == null) {
            return Collections.emptyList();
        }

        List<ScoreRecordDto> records = new ArrayList<ScoreRecordDto>();
        for (String value : values) {
            records.add(readJson(value, ScoreRecordDto.class));
        }
        return records;
    }

    private boolean containsPlayer(List<PlayerDto> players, Long userId) {
        for (PlayerDto player : players) {
            if (player.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    private void clearRoomRedis(String roomCode) {
        redisTemplate.delete(metaKey(roomCode));
        redisTemplate.delete(playersKey(roomCode));
        redisTemplate.delete(playerOrderKey(roomCode));
        redisTemplate.delete(recordsKey(roomCode));
    }

    private String generateRoomCode() {
        for (int i = 0; i < 20; i++) {
            String code = String.format("%04d", random.nextInt(10000));
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(metaKey(code)))) {
                return code;
            }
        }
        throw new BusinessException("房间号生成失败，请重试");
    }

    private String metaKey(String roomCode) {
        return ROOM_PREFIX + roomCode + ":meta";
    }

    private String playersKey(String roomCode) {
        return ROOM_PREFIX + roomCode + ":players";
    }

    private String playerOrderKey(String roomCode) {
        return ROOM_PREFIX + roomCode + ":player-order";
    }

    private String recordsKey(String roomCode) {
        return ROOM_PREFIX + roomCode + ":records";
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new BusinessException("数据序列化失败");
        }
    }

    private <T> T readJson(String value, Class<T> type) {
        try {
            return objectMapper.readValue(value, type);
        } catch (Exception ex) {
            throw new BusinessException("数据读取失败");
        }
    }
}
