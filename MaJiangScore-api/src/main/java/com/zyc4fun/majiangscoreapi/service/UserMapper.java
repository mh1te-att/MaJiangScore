package com.zyc4fun.majiangscoreapi.service;

import com.zyc4fun.majiangscoreapi.entity.AppUser;
import com.zyc4fun.majiangscoreapi.dto.ScoreHistoryDtos;
import com.zyc4fun.majiangscoreapi.dto.UserDto;

import java.util.List;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto toDto(AppUser user) {
        return toDto(user, null);
    }

    public static UserDto toDto(AppUser user, List<ScoreHistoryDtos.ScoreHistoryDto> histories) {
        return new UserDto(
                user.getId(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getWinCount(),
                user.getLoseCount(),
                user.getTotalScore(),
                histories
        );
    }
}
