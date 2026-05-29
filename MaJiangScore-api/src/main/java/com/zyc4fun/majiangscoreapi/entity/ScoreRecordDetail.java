package com.zyc4fun.majiangscoreapi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "score_record_detail")
public class ScoreRecordDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recordId;

    @Lob
    @Column(nullable = false)
    private String text;

    public ScoreRecordDetail() {
    }

    public ScoreRecordDetail(Long recordId, String text) {
        this.recordId = recordId;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getText() {
        return text;
    }
}
