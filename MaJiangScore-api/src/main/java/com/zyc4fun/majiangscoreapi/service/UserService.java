package com.zyc4fun.majiangscoreapi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zyc4fun.majiangscoreapi.common.BusinessException;
import com.zyc4fun.majiangscoreapi.entity.AppUser;
import com.zyc4fun.majiangscoreapi.dto.UserDto;
import com.zyc4fun.majiangscoreapi.dto.UserDtos;
import com.zyc4fun.majiangscoreapi.dto.ScoreHistoryDtos;
import com.zyc4fun.majiangscoreapi.entity.ScoreRecord;
import com.zyc4fun.majiangscoreapi.entity.ScoreRecordDetail;
import com.zyc4fun.majiangscoreapi.repository.AppUserRepository;
import com.zyc4fun.majiangscoreapi.repository.ScoreRecordDetailRepository;
import com.zyc4fun.majiangscoreapi.repository.ScoreRecordRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final AppUserRepository appUserRepository;
    private final ScoreRecordRepository scoreRecordRepository;
    private final ScoreRecordDetailRepository scoreRecordDetailRepository;
    private final ObjectMapper objectMapper;

    public UserService(AppUserRepository appUserRepository, ScoreRecordRepository scoreRecordRepository,
                       ScoreRecordDetailRepository scoreRecordDetailRepository, ObjectMapper objectMapper) {
        this.appUserRepository = appUserRepository;
        this.scoreRecordRepository = scoreRecordRepository;
        this.scoreRecordDetailRepository = scoreRecordDetailRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public UserDto me(AppUser user) {
        List<ScoreRecord> records = scoreRecordRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(0, 20));
        List<ScoreHistoryDtos.ScoreHistoryDto> histories = new ArrayList<ScoreHistoryDtos.ScoreHistoryDto>();
        for (ScoreRecord record : records) {
            ScoreRecordDetail detail = scoreRecordDetailRepository.findById(record.getRecordDetailId()).orElse(null);
            if (detail == null) {
                continue;
            }
            histories.add(new ScoreHistoryDtos.ScoreHistoryDto(
                    record.getId(),
                    record.getCreatedAt(),
                    readPlayers(detail.getText())
            ));
        }
        return UserMapper.toDto(user, histories);
    }

    @Transactional
    public UserDto updateProfile(AppUser user, UserDtos.UpdateProfileRequest request) {
        if (request == null) {
            throw new BusinessException("请求不能为空");
        }
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getAvatarUrl())) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        return me(appUserRepository.save(user));
    }

    private List<ScoreHistoryDtos.ScoreHistoryPlayerDto> readPlayers(String text) {
        try {
            return objectMapper.readValue(text, new TypeReference<List<ScoreHistoryDtos.ScoreHistoryPlayerDto>>() {
            });
        } catch (Exception ex) {
            return new ArrayList<ScoreHistoryDtos.ScoreHistoryPlayerDto>();
        }
    }
}
