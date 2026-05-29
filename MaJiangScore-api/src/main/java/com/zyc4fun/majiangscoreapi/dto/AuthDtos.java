package com.zyc4fun.majiangscoreapi.dto;

public class AuthDtos {
    public static class WechatLoginRequest {
        private String code;
        private String nickname;
        private String avatarUrl;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }

    public static class LoginResponse {
        private String token;
        private UserDto user;

        public LoginResponse(String token, UserDto user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        public UserDto getUser() {
            return user;
        }
    }
}
