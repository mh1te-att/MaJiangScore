package com.zyc4fun.majiangscoreapi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String openId;

    @Column(nullable = false, length = 40)
    private String nickname;

    @Column(length = 500)
    private String avatarUrl;

    @Column(nullable = false)
    private int winCount;

    @Column(nullable = false)
    private int loseCount;

    @Column(nullable = false)
    private int totalScore;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public AppUser() {
    }

    public AppUser(String openId, String nickname, String avatarUrl) {
        this.openId = openId;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getOpenId() {
        return openId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.updatedAt = LocalDateTime.now();
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

    public void addTotalScore(int delta) {
        this.totalScore += delta;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordSettlementResult(int finalScore, boolean winner, boolean loser) {
        this.totalScore += finalScore;
        if (winner) {
            this.winCount++;
        }
        if (loser) {
            this.loseCount++;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
