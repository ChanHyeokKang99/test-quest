package com.justteam.test_quest_api.api.auth;

import com.justteam.test_quest_api.api.file.FirebaseStorageService;
import com.justteam.test_quest_api.api.user.entity.User;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.justteam.test_quest_api.api.user.service.UserService;
import com.justteam.test_quest_api.api.user.dto.UserLoginDto;
import com.justteam.test_quest_api.api.user.dto.UserRegisterDto;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import com.justteam.test_quest_api.jwt.dto.TokenDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;

    private final FirebaseStorageService firebaseStorageService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ApiResponseDto<String> userRegister(
            @Valid UserRegisterDto userRegisterDto
    ) {
        try {
            String imageUrl = null;
            MultipartFile profileImage = userRegisterDto.getProfileImage();
            // DTO 내부에 있는 profileImage 필드 사용
            if (profileImage != null && !profileImage.isEmpty()) {
                imageUrl = firebaseStorageService.uploadImage(profileImage);
                userRegisterDto.setProfileImg(imageUrl);
            }

            userService.registerUser(userRegisterDto);
            return ApiResponseDto.defaultOk();

        } catch (IOException e) {
            log.error("Image upload failed during registration: {}", e.getMessage());
            return ApiResponseDto.createError("IMAGE_UPLOAD_FAILED", "프로필 이미지 업로드에 실패했습니다.");
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage());
            return ApiResponseDto.createError("REGISTRATION_FAILED", "사용자 등록에 실패했습니다.");
        }
    }
    
    @PostMapping(value = "/login")
    private ApiResponseDto<TokenDto.AccessRefreshToken> userLogin(@RequestBody UserLoginDto userLoginDto) {
        TokenDto.AccessRefreshToken token = userService.loginUser(userLoginDto);
        return ApiResponseDto.createOk(token);
    }

    @PostMapping(value = "/refresh")
    @SecurityRequirement(name = "BearerAuth")
    public ApiResponseDto<TokenDto.AccessToken> refresh(@Parameter(hidden = true) @RequestHeader("Authorization") String refreshToken) {
        TokenDto.AccessToken token = userService.refresh(refreshToken);
        return ApiResponseDto.createOk(token);
    }
}
