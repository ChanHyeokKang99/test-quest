package com.justteam.test_quest_api.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import com.justteam.test_quest_api.common.exception.InvalidTokenException;
import com.justteam.test_quest_api.jwt.TokenGenerator;
import com.justteam.test_quest_api.jwt.authentication.JwtAuthentication;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenGenerator tokenGenerator;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = tokenGenerator.getToken(request);

            if(token != null) {
                JwtAuthentication authentication = tokenGenerator.validateToken(token);
                if(authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            handleTokenException(response, e.getMessage(), "401");
        } catch (ExpiredJwtException e) {
            handleTokenException(response, "토큰이 만료되었습니다.", "401");
        } catch (Exception e) {
            log.error("JWT 인증 처리 중 오류 발생", e);
            filterChain.doFilter(request, response);
        }
    }

    private void handleTokenException(HttpServletResponse response, String message, String errorCode) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        ApiResponseDto<String> errorResponse = ApiResponseDto.createError(errorCode, message);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        
        response.getWriter().write(jsonResponse);
    }
}