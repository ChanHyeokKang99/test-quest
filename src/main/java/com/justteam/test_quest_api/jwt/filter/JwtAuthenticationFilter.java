package com.justteam.test_quest_api.jwt.filter;

import com.justteam.test_quest_api.api.user.entity.User;
import com.justteam.test_quest_api.api.user.repository.UserRepository;
import com.justteam.test_quest_api.api.user.service.UserService;
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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = tokenGenerator.getToken(request);

        try {
            // 핵심 수정: jwtToken이 null이 아닐 때만 유효성 검증 로직을 실행합니다.
            if (jwtToken != null) { // 이 조건문이 빠져 있었습니다!
                String userId = tokenGenerator.validateJwtToken(jwtToken);
                log.debug("userId : {}", userId);

                if (userId != null) {

                    Optional<User> user = userRepository.findById(userId);
                    if(user.isPresent()){
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                user, null
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("인증된 사용자: {}", user.get().getUserId());

                    }else {
                        log.debug("존재하지 않은 회원입니다.");
                    }
                } else {
                    log.debug("요청에 JWT 토큰이 없습니다. URL: {}", request.getRequestURI());
                }
            }else {
                log.debug("사용자 정보가 없습니다.");
            }
        } catch (Exception e) {
            log.error("JWT 인증 중 오류 발생: "+ e);
        }
     filterChain.doFilter(request, response);
    }
}