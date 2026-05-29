package com.zyc4fun.majiangscoreapi.dto;

import java.time.LocalDateTime;

public class ScoreRecordDto {
    private Long id;
    private String roomCode;
    private Long playerId;
    private String playerName;
    private int scoreChange;
    private LocalDateTime createdAt;

    public ScoreRecordDto() {
    }

    public ScoreRecordDto(Long id, String roomCode, Long playerId, String playerName, int scoreChange, LocalDateTime createdAt) {
        this.id = id;
        this.roomCode = roomCode;
        this.playerId = playerId;
        this.playerName = playerName;
        this.scoreChange = scoreChange;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScoreChange() {
        return scoreChange;
    }

    public void setScoreChange(int scoreChange) {
        this.scoreChange = scoreChange;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
