package com.zyc4fun.majiangscoreapi.controller;

import com.zyc4fun.majiangscoreapi.common.ApiResponse;
import com.zyc4fun.majiangscoreapi.dto.AuthDtos;
import com.zyc4fun.majiangscoreapi.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/wechat")
    public ApiResponse<AuthDtos.LoginResponse> wechatLogin(@RequestBody AuthDtos.WechatLoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }
}
