CREATE DATABASE IF NOT EXISTS `majiang_score`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `majiang_score`;

DROP TABLE IF EXISTS `settlement_player`;
DROP TABLE IF EXISTS `game_settlement`;
DROP TABLE IF EXISTS `score_record`;
DROP TABLE IF EXISTS `player`;
DROP TABLE IF EXISTS `room`;
DROP TABLE IF EXISTS `score_record_detail`;

CREATE TABLE IF NOT EXISTS `app_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `open_id` VARCHAR(80) NOT NULL,
  `nickname` VARCHAR(40) NOT NULL,
  `avatar_url` VARCHAR(500) DEFAULT NULL,
  `win_count` INT NOT NULL DEFAULT 0,
  `lose_count` INT NOT NULL DEFAULT 0,
  `total_score` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME(6) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_user_open_id` (`open_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `score_record_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `record_id` BIGINT NOT NULL,
  `text` LONGTEXT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_score_record_detail_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `score_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `record_detail_id` BIGINT NOT NULL,
  `created_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_score_record_user_created_at` (`user_id`, `created_at`),
  KEY `idx_score_record_detail_id` (`record_detail_id`),
  CONSTRAINT `fk_score_record_user`
    FOREIGN KEY (`user_id`) REFERENCES `app_user` (`id`),
  CONSTRAINT `fk_score_record_detail`
    FOREIGN KEY (`record_detail_id`) REFERENCES `score_record_detail` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
