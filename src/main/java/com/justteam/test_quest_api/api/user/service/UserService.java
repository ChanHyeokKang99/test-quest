package com.justteam.test_quest_api.api.user.service;

import java.util.Optional;

import com.justteam.test_quest_api.api.user.dto.UserInfoDto;
import com.justteam.test_quest_api.api.user.dto.UserUpdateDto;
import com.justteam.test_quest_api.common.exception.BadParameter;
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

    @Transactional
    public TokenDto.AccessToken refresh(String refreshToken) {
        String userId = tokenGenerator.validateJwtRefreshToken(refreshToken);
        if (userId == null) {
            throw new Error("토큰이 유효하지 않습니다.");
        }
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new Error("사용자를 찾을 수 없습니다.");
        }

        return tokenGenerator.generateAccessToken(userId, "WEB");
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
