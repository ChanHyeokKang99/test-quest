package com.justteam.test_quest_api.api.user.controller;

import com.justteam.test_quest_api.api.file.FirebaseStorageService;
import com.justteam.test_quest_api.api.user.dto.UserInfoDto;
import com.justteam.test_quest_api.api.user.dto.UserUpdateDto;
import com.justteam.test_quest_api.api.user.entity.User;
import com.justteam.test_quest_api.api.user.service.UserService;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import com.justteam.test_quest_api.common.web.context.RequestHeaderUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/user")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FirebaseStorageService firebaseStorageService;

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ApiResponseDto<String> updateUser(@Valid UserUpdateDto userUpdateDto) {
        try {
            String imageUrl = null;
            MultipartFile profileImage = userUpdateDto.getProfileImage();

            if(profileImage != null) {
                imageUrl = firebaseStorageService.uploadImage(profileImage);
                userUpdateDto.setProfileImg(imageUrl);
            }
            userService.updateUser(userUpdateDto);

            return ApiResponseDto.defaultOk();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/getinfo")
    private ApiResponseDto<UserInfoDto> getUser() {
        if(RequestHeaderUtils.getUserId()==null){
            return ApiResponseDto.createError("403","사용자 정보없음");
        }
        UserInfoDto user = userService.getUserInfo(RequestHeaderUtils.getUserId());
        return ApiResponseDto.createOk(user);
    }

    @PostMapping(value = "/delete")
    private ApiResponseDto deleteUser() {
        try {
            userService.deleteUser(RequestHeaderUtils.getUserId());
            return ApiResponseDto.defaultOk();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
