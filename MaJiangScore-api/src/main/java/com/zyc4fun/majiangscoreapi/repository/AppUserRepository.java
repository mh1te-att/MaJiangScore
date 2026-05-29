package com.zyc4fun.majiangscoreapi.repository;

import com.zyc4fun.majiangscoreapi.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByOpenId(String openId);
}
