package com.justteam.test_quest_api.api.user.service;

import java.util.Optional;

import com.justteam.test_quest_api.api.user.dto.UserInfoDto;
import com.justteam.test_quest_api.api.user.dto.UserUpdateDto;
import com.justteam.test_quest_api.common.exception.BadParameter;
import com.justteam.test_quest_api.common.exception.InvalidTokenException;
import com.justteam.test_quest_api.common.exception.NotFound;
import com.justteam.test_quest_api.common.web.context.RequestHeaderUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.justteam.test_quest_api.api.user.dto.UserLoginDto;
import com.justteam.test_quest_api.api.user.dto.UserRegisterDto;
import com.justteam.test_quest_api.api.user.entity.User;
import com.justteam.test_quest_api.api.user.repository.UserRepository;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import com.justteam.test_quest_api.jwt.TokenGenerator;
import com.justteam.test_quest_api.jwt.dto.TokenDto;
import com.justteam.test_quest_api.jwt.hash.SecureHashUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;

    @Transactional
    public ApiResponseDto<String> registerUser(UserRegisterDto userRegisterDto) {
        User user = userRegisterDto.toEntity();
        userRepository.save(user);
        return ApiResponseDto.defaultOk();
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
    public TokenDto.AccessToken refresh(String refreshToken) {
        try {
            String userId = tokenGenerator.validateJwtRefreshToken(refreshToken);
            if (userId == null) {
                throw new InvalidTokenException("유효하지 않은 리프레시 토큰입니다.");
            }
            
            // 사용자 존재 여부 확인
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFound("사용자를 찾을 수 없습니다."));
            
            // 새로운 액세스 토큰 발급
            return tokenGenerator.generateAccessToken(userId, "WEB");
        } catch (InvalidTokenException e) {
            log.error("토큰 갱신 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("토큰 갱신 중 오류 발생: {}", e.getMessage());
            throw new InvalidTokenException("토큰 갱신에 실패했습니다.");
        }
    }

    public ApiResponseDto<String> updateUser(@Valid UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(RequestHeaderUtils.getUserId()).orElse(null);
        if (user == null) {
            new BadParameter("회원정보가 존재하지 않습니다");
        }
        if(userUpdateDto.getNickname() != null) {
            user.setNickname(userUpdateDto.getNickname());
        }
        if(userUpdateDto.getProfileImg() != null) {
            user.setProfileImg(userUpdateDto.getProfileImg());
        }
        userRepository.save(user);
        return ApiResponseDto.defaultOk();
    }

    public ApiResponseDto<String> deleteUser(String userId) {
        Optional<User> user = userRepository.findById(userId);
        userRepository.deleteById(user.get().getUserId());
        return ApiResponseDto.defaultOk();
    }

    public UserInfoDto getUserInfo(String userId) {
        return userRepository.findUserInfoDtoByUserId(userId)
                .orElseThrow(() -> new NotFound("User not found with ID: " + userId));
    }
}
