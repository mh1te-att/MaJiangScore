package com.zyc4fun.majiangscoreapi.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ScoreHistoryDtos {
    public static class ScoreHistoryDto {
        private Long id;
        private LocalDateTime createdAt;
        private List<ScoreHistoryPlayerDto> players;

        public ScoreHistoryDto(Long id, LocalDateTime createdAt, List<ScoreHistoryPlayerDto> players) {
            this.id = id;
            this.createdAt = createdAt;
            this.players = players;
        }

        public Long getId() {
            return id;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public List<ScoreHistoryPlayerDto> getPlayers() {
            return players;
        }
    }

    public static class ScoreHistoryPlayerDto {
        private String userName;
        private int score;

        public ScoreHistoryPlayerDto() {
        }

        public ScoreHistoryPlayerDto(String userName, int score) {
            this.userName = userName;
            this.score = score;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
