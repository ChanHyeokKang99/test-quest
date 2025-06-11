package com.justteam.test_quest_api.api.auth;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.justteam.test_quest_api.api.user.UserService;
import com.justteam.test_quest_api.api.user.dto.UserLoginDto;
import com.justteam.test_quest_api.api.user.dto.UserRefreshDto;
import com.justteam.test_quest_api.api.user.dto.UserRegisterDto;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import com.justteam.test_quest_api.jwt.dto.TokenDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping(value = "/register")
    private ApiResponseDto<String> userRegister(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        userService.registerUser(userRegisterDto);
        return ApiResponseDto.createOk(null);
    }
    
    @PostMapping(value = "/login")
    private ApiResponseDto<TokenDto.AccessRefreshToken> userLogin(@RequestBody UserLoginDto userLoginDto) {
        TokenDto.AccessRefreshToken token = userService.loginUser(userLoginDto);
        return ApiResponseDto.createOk(token);
    }

    @PostMapping(value = "/refresh")
    public ApiResponseDto<TokenDto.AccessToken> refresh(@RequestBody UserRefreshDto userRefreshDto) {
        TokenDto.AccessToken token = userService.refresh(userRefreshDto);
        return ApiResponseDto.createOk(token);
    }
}
