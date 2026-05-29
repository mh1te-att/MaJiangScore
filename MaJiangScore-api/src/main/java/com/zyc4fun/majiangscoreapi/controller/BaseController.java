package com.zyc4fun.majiangscoreapi.controller;

import org.springframework.util.StringUtils;

public abstract class BaseController {
    protected String tokenOf(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        if (authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}
