package com.justteam.test_quest_api.api.user.dto;

import com.justteam.test_quest_api.api.user.entity.User;
import com.justteam.test_quest_api.jwt.hash.SecureHashUtils;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "사용자 등록 DTO")
public class UserRegisterDto {

    @NotBlank(message = "이름을 입력하세요")
    @Schema(description = "사용자 이름", required = true, nullable = false)
    private String name;

    @NotBlank(message = "이메일을 입력하세요")
    @Schema(description = "이메일", required = true, nullable = false)
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요")
    @Schema(description = "비밀번호", required = true, nullable = false)
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요")
    @Schema(description = "닉네임", required = true, nullable = false)
    private String nickname;

    @Schema(description = "프로필 이미지 URL", hidden = true)
    private String profileImg;

    @Schema(description = "프로필 이미지 파일", nullable = true)
    private MultipartFile profileImage;

    public User toEntity() {
        User user = new User();

        user.setEmail(this.email);
        user.setNickname(this.nickname);
        user.setName(this.name);
        user.setPassword(SecureHashUtils.hash(this.password));
        user.setProfileImg(this.profileImg);
        return user;
    }
}
