package com.justteam.test_quest_api.jwt.filter;

import com.justteam.test_quest_api.jwt.TokenGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenGenerator tokenGenerator;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = tokenGenerator.getToken(request);

        try {
            // 핵심 수정: jwtToken이 null이 아닐 때만 유효성 검증 로직을 실행합니다.
            if (jwtToken != null) { // 이 조건문이 빠져 있었습니다!
                String userId = tokenGenerator.validateJwtToken(jwtToken);
                log.debug("userId : {}", userId);

                if (userId != null && !userId.trim().isEmpty()) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("인증된 사용자: {}", userDetails.getUsername());
                } else {
                    log.warn("JWT 토큰은 유효하지만 사용자 ID를 추출할 수 없거나 비어 있습니다. 토큰: {}", jwtToken);
                }
            } else {
                log.debug("요청에 JWT 토큰이 없습니다. URL: {}", request.getRequestURI());
            }
        } catch (Exception e) {
            log.error("JWT 인증 중 오류 발생: {}",e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }
}