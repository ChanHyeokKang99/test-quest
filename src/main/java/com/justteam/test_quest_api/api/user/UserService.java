package com.justteam.test_quest_api.api.user;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.justteam.test_quest_api.api.user.dto.UserLoginDto;
import com.justteam.test_quest_api.api.user.dto.UserRefreshDto;
import com.justteam.test_quest_api.api.user.dto.UserRegisterDto;
import com.justteam.test_quest_api.api.user.entity.User;
import com.justteam.test_quest_api.api.user.repository.UserRepository;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import com.justteam.test_quest_api.jwt.TokenGenerator;
import com.justteam.test_quest_api.jwt.dto.TokenDto;
import com.justteam.test_quest_api.jwt.hash.SecureHashUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;

    @Transactional
    public ApiResponseDto registerUser(UserRegisterDto userRegisterDto) {
        User user = userRegisterDto.toEntity();
        userRepository.save(user);
        return ApiResponseDto.createOk(user);
    }
    @Transactional(readOnly = true)
    public TokenDto.AccessRefreshToken loginUser(UserLoginDto userLoginDto) {
        User user = userRepository.findByEmail(userLoginDto.getEmail());
        if (user == null) {
            throw new Error("아이디 또는 비밀번호를 확인하세요");
        }
        if (!SecureHashUtils.matches(userLoginDto.getPassword(), user.getPassword())) {
            throw new Error("비밀번호를 확인하세요");
        }
        return tokenGenerator.generateAccessRefreshToken(user.getUserId(), "WEB");
    }

    @Transactional(readOnly = true)
    public TokenDto.AccessToken refresh(UserRefreshDto refreshDto) {
        String userId = tokenGenerator.validateJwtToken(refreshDto.getToken());
        if (userId == null) {
            throw new Error("토큰이 유효하지 않습니다.");
        }

        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            throw new Error("사용자를 찾을 수 없습니다.");
        }

        return tokenGenerator.generateAccessToken(userId, "WEB");
    }
}
