package com.zyc4fun.majiangscoreapi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "score_record")
public class ScoreRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long recordDetailId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ScoreRecord() {
    }

    public ScoreRecord(Long userId, Long recordDetailId, LocalDateTime createdAt) {
        this.userId = userId;
        this.recordDetailId = recordDetailId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRecordDetailId() {
        return recordDetailId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
