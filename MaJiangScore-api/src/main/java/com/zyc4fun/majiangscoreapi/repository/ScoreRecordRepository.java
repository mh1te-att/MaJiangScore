package com.zyc4fun.majiangscoreapi.repository;

import com.zyc4fun.majiangscoreapi.entity.ScoreRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRecordRepository extends JpaRepository<ScoreRecord, Long> {
    List<ScoreRecord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
