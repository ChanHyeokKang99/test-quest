package com.justteam.test_quest_api.jwt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import com.justteam.test_quest_api.jwt.authentication.JwtAuthentication;
import com.justteam.test_quest_api.jwt.authentication.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.justteam.test_quest_api.jwt.dto.TokenDto;
import com.justteam.test_quest_api.jwt.props.JwtConfigProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenGenerator {
    private final JwtConfigProperties configProperties;

    private volatile SecretKey secretKey;

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            synchronized (this) {
                if (secretKey == null) {
                    secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(configProperties.getSecretKey()));
                }
            }
        }
        return secretKey;
    }
    
    public TokenDto.AccessToken generateAccessToken(String userId, String deviceType) {
        TokenDto.JwtToken jwtToken = this.generateToken(userId, deviceType, false);
        return new TokenDto.AccessToken(jwtToken);
    }

    public TokenDto.AccessRefreshToken generateAccessRefreshToken(String userId, String deviceType) {
        TokenDto.JwtToken accessJwtToken = this.generateToken(userId, deviceType, false);
        TokenDto.JwtToken refreshJwtToken = this.generateToken(userId, deviceType, true);
        return new TokenDto.AccessRefreshToken(accessJwtToken, refreshJwtToken);
    }

    public TokenDto.JwtToken generateToken(String userId, String deviceType, boolean refreshToken) {
        int tokenExpiresIn = tokenExpiresIn(refreshToken, deviceType);
        String tokenType = refreshToken ? "refresh" : "access";

        String token = Jwts.builder()
                .issuer("justteam")
                .subject(userId)
                .claim("userId", userId)
                .claim("tokenType", tokenType)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiresIn * 1000L))
                .signWith(getSecretKey())
                .header().add("typ", "JWT")
                .and()
                .compact();

        return new TokenDto.JwtToken(token, tokenExpiresIn);
    }

    public String validateJwtRefreshToken(String refreshToken) {
        // verifyAndGetClaims 메서드에서 이미 토큰 유효성 검증 및 파싱이 이루어집니다.
        final Claims claims = this.verifyAndGetClaims(refreshToken);
        if (claims == null) {
            return null; // 클레임을 얻지 못했다면 유효하지 않은 토큰
        }

        // 토큰 만료 시간 검증
        Date expirationDate = claims.getExpiration();
        if (expirationDate == null || expirationDate.before(new Date())) {
            log.debug("Refresh Token이 만료되었습니다. userId: {}", claims.get("userId", String.class));
            return null;
        }

        String userId = claims.get("userId", String.class);
        String tokenType = claims.get("tokenType", String.class);

        // 토큰 타입이 "refresh"인지 확인
        if (!"refresh".equals(tokenType)) {
            log.debug("Token Type이 'refresh'가 아닙니다. tokenType: {}", tokenType);
            return null;
        }

        return userId;
    }

    public JwtAuthentication validateToken(String token) {
        String userId = null;

        final Claims claims = this.verifyAndGetClaims(token);
        if (claims == null) {
            return null;
        }

        Date expirationDate = claims.getExpiration();
        if (expirationDate == null || expirationDate.before(new Date())) {
            return null;
        }

        userId = claims.get("userId", String.class);

        String tokenType = claims.get("tokenType", String.class);
        if (!"access".equals(tokenType)) {
            return null;
        }

        UserPrincipal principal = new UserPrincipal(userId);

        return new JwtAuthentication(principal, token, getGrantedAuthorities("user"));
    }

    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (role != null) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }

        return grantedAuthorities;
    }


    private Claims verifyAndGetClaims(String token) {
        Claims claims = null;

        try {
            claims = Jwts.parser()
            .verifyWith(getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private int tokenExpiresIn(boolean refreshToken, String deviceType) {
        int expiresIn = 60 * 15;

        if (refreshToken) {
            if (deviceType != null) {
                if (deviceType.equals("WEB")) {
                    expiresIn = configProperties.getExpiresIn();
                } else if (deviceType.equals("MOBILE")) {
                    expiresIn = configProperties.getMobileExpiresIn();
                } else {
                    expiresIn = configProperties.getExpiresIn();
                }
            } else {
                expiresIn = configProperties.getExpiresIn();
            }
        }

        return expiresIn;
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(configProperties.getHeader());
    }
}
