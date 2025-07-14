package com.justteam.test_quest_api.api.auth;

import com.justteam.test_quest_api.api.file.FirebaseStorageService;
import com.justteam.test_quest_api.api.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
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
    @Operation(summary = "로그인", description = "사용자 로그인을 처리하고 액세스 토큰과 리프레시 토큰을 발급합니다.")
    private ApiResponseDto<TokenDto.AccessRefreshToken> userLogin(@RequestBody UserLoginDto userLoginDto) {
        TokenDto.AccessRefreshToken token = userService.loginUser(userLoginDto);
        return ApiResponseDto.createOk(token);
    }

    @PostMapping(value = "/refresh")
    @Operation(
        summary = "액세스 토큰 갱신", 
        description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "토큰 갱신 성공",
                content = @Content(schema = @Schema(implementation = TokenDto.AccessToken.class))
            ),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
        }
    )
    public ApiResponseDto<TokenDto.AccessToken> refresh(
            @Parameter(description = "리프레시 토큰 (Bearer 형식)", required = true)
            @RequestHeader("Authorization") String refreshToken) {
        TokenDto.AccessToken token = userService.refresh(refreshToken);
        return ApiResponseDto.createOk(token);
    }
}
