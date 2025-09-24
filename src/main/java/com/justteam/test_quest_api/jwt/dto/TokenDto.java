package com.justteam.test_quest_api.jwt.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenDto {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JwtToken {
        private String token;
        private Integer expiresIn;
    }

    @Getter
    @RequiredArgsConstructor
    public static class AccessToken {
        private final JwtToken access;
    }

    // 로그인시
    @Getter
    @RequiredArgsConstructor
    public static class AccessRefreshToken {
        private final JwtToken access;
        private final JwtToken refresh;
        private final String userId;
    }
    
}
