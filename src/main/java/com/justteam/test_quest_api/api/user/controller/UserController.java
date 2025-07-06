package com.justteam.test_quest_api.api.user.controller;

import com.justteam.test_quest_api.api.file.FirebaseStorageService;
import com.justteam.test_quest_api.api.user.dto.UserUpdateDto;
import com.justteam.test_quest_api.api.user.service.UserService;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;
    private final FirebaseStorageService firebaseStorageService;

    @PostMapping(name = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PostMapping(name = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    private ApiResponseDto<String> deleteUser(@RequestParam String userId) {
        try {

            return ApiResponseDto.defaultOk();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
