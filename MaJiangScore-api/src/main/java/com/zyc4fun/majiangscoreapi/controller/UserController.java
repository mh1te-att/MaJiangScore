package com.zyc4fun.majiangscoreapi.controller;

import com.zyc4fun.majiangscoreapi.common.ApiResponse;
import com.zyc4fun.majiangscoreapi.entity.AppUser;
import com.zyc4fun.majiangscoreapi.dto.UserDto;
import com.zyc4fun.majiangscoreapi.dto.UserDtos;
import com.zyc4fun.majiangscoreapi.service.AuthService;
import com.zyc4fun.majiangscoreapi.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController {
    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserDto> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        AppUser user = authService.requireUser(tokenOf(authorization));
        return ApiResponse.ok(userService.me(user));
    }

    @PutMapping("/me")
    public ApiResponse<UserDto> updateMe(@RequestHeader(value = "Authorization", required = false) String authorization,
                                         @RequestBody UserDtos.UpdateProfileRequest request) {
        AppUser user = authService.requireUser(tokenOf(authorization));
        return ApiResponse.ok(userService.updateProfile(user, request));
    }
}
