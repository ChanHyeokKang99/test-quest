package com.justteam.test_quest_api.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserUpdateDto {
    @NotBlank(message = "회원아이디를 입력하세요")
    private String userId;
    private String nickname;
    private MultipartFile profileImage;

    @Schema(description = "수정 프로필 이미지(선택)", hidden = true)
    private String profileImg;
}
