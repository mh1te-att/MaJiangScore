package com.zyc4fun.majiangscoreapi.dto;

public class UserDto {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private int winCount;
    private int loseCount;
    private int totalScore;
    private double winRate;
    private java.util.List<ScoreHistoryDtos.ScoreHistoryDto> histories;

    public UserDto(Long id, String nickname, String avatarUrl, int winCount, int loseCount, int totalScore) {
        this(id, nickname, avatarUrl, winCount, loseCount, totalScore, new java.util.ArrayList<ScoreHistoryDtos.ScoreHistoryDto>());
    }

    public UserDto(Long id, String nickname, String avatarUrl, int winCount, int loseCount, int totalScore,
                   java.util.List<ScoreHistoryDtos.ScoreHistoryDto> histories) {
        this.id = id;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.winCount = winCount;
        this.loseCount = loseCount;
        this.totalScore = totalScore;
        this.histories = histories == null ? new java.util.ArrayList<ScoreHistoryDtos.ScoreHistoryDto>() : histories;
        int total = winCount + loseCount;
        this.winRate = total == 0 ? 0 : (double) winCount / total;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getWinCount() {
        return winCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public double getWinRate() {
        return winRate;
    }

    public java.util.List<ScoreHistoryDtos.ScoreHistoryDto> getHistories() {
        return histories;
    }
}
