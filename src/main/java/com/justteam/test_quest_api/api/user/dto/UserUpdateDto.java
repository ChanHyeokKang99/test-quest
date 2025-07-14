package com.justteam.test_quest_api.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "사용자 정보 수정 DTO")
public class UserUpdateDto {
    @Schema(description = "닉네임", nullable = true)
    private String nickname;
    
    @Schema(description = "프로필 이미지 파일", nullable = true)
    private MultipartFile profileImage;

    @Schema(description = "프로필 이미지 URL", hidden = true)
    private String profileImg;
}
