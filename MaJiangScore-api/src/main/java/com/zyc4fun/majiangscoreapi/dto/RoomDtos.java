package com.zyc4fun.majiangscoreapi.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RoomDtos {
    public static class CreateRoomRequest {
    }

    public static class JoinRoomRequest {
        private String roomCode;

        public String getRoomCode() {
            return roomCode;
        }

        public void setRoomCode(String roomCode) {
            this.roomCode = roomCode;
        }
    }

    public static class SpendScoreRequest {
        private Long playerId;
        private Integer amount;

        public Long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(Long playerId) {
            this.playerId = playerId;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }

    public static class BatchSpendRequest {
        private Map<Long, Integer> spends;

        public Map<Long, Integer> getSpends() {
            return spends;
        }

        public void setSpends(Map<Long, Integer> spends) {
            this.spends = spends;
        }
    }

    public static class RoomDto {
        private String roomCode;
        private Long ownerUserId;
        private String ownerName;
        private Long currentPlayerId;
        private LocalDateTime createdAt;
        private List<PlayerDto> players;
        private List<ScoreRecordDto> records;

        public RoomDto(String roomCode, Long ownerUserId, String ownerName, Long currentPlayerId,
                       LocalDateTime createdAt, List<PlayerDto> players, List<ScoreRecordDto> records) {
            this.roomCode = roomCode;
            this.ownerUserId = ownerUserId;
            this.ownerName = ownerName;
            this.currentPlayerId = currentPlayerId;
            this.createdAt = createdAt;
            this.players = players;
            this.records = records;
        }

        public String getRoomCode() {
            return roomCode;
        }

        public Long getOwnerUserId() {
            return ownerUserId;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public Long getCurrentPlayerId() {
            return currentPlayerId;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public List<PlayerDto> getPlayers() {
            return players;
        }

        public List<ScoreRecordDto> getRecords() {
            return records;
        }
    }
}
