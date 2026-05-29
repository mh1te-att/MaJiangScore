package com.zyc4fun.majiangscoreapi.dto;

public class PlayerDto {
    private Long id;
    private Long userId;
    private String name;
    private String avatarUrl;
    private int win;
    private int lose;
    private int score;

    public PlayerDto() {
    }

    public PlayerDto(Long id, Long userId, String name, String avatarUrl, int win, int lose, int score) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.win = win;
        this.lose = lose;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
