package com.justteam.test_quest_api.api.user.dto;

import com.justteam.test_quest_api.api.user.entity.User;
import com.justteam.test_quest_api.jwt.hash.SecureHashUtils;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRegisterDto {

    @NotBlank(message = "이름을 입력하세요")
    private String name;

    @NotBlank(message = "이메일을 입력하세요")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요")
    private String nickname;

    @Schema(hidden = true)
    private String profileImg;

    private MultipartFile profileImage; // <-- MultipartFile 필드 추가

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
