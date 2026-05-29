package com.zyc4fun.majiangscoreapi.service;

import com.zyc4fun.majiangscoreapi.common.BusinessException;
import com.zyc4fun.majiangscoreapi.entity.AppUser;
import com.zyc4fun.majiangscoreapi.dto.AuthDtos;
import com.zyc4fun.majiangscoreapi.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    private static final String TOKEN_PREFIX = "majiang:token:";
    private static final long TOKEN_TTL_DAYS = 30;

    private final AppUserRepository appUserRepository;
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    @Value("${wx.miniapp.app-id:}")
    private String appId;

    @Value("${wx.miniapp.secret:}")
    private String secret;

    public AuthService(AppUserRepository appUserRepository, StringRedisTemplate redisTemplate, RestTemplate restTemplate) {
        this.appUserRepository = appUserRepository;
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public AuthDtos.LoginResponse login(AuthDtos.WechatLoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getCode())) {
            throw new BusinessException("缺少微信登录 code");
        }

        String openId = fetchOpenId(request.getCode());
        AppUser user = appUserRepository.findByOpenId(openId)
                .orElseGet(() -> appUserRepository.save(new AppUser(openId, defaultNickname(request.getNickname()), request.getAvatarUrl())));

        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getAvatarUrl())) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, String.valueOf(user.getId()), TOKEN_TTL_DAYS, TimeUnit.DAYS);
        return new AuthDtos.LoginResponse(token, UserMapper.toDto(user));
    }

    public AppUser requireUser(String token) {
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("请先登录");
        }

        String userId = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException("登录已过期");
        }

        return appUserRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    private String fetchOpenId(String code) {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(secret)) {
            return "dev-" + code;
        }

        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId
                + "&secret=" + secret
                + "&js_code=" + code
                + "&grant_type=authorization_code";
        Map response = restTemplate.getForObject(url, Map.class);
        if (response == null || response.get("openid") == null) {
            throw new BusinessException("微信登录失败");
        }
        return String.valueOf(response.get("openid"));
    }

    private String defaultNickname(String nickname) {
        return StringUtils.hasText(nickname) ? nickname : "微信用户";
    }
}
