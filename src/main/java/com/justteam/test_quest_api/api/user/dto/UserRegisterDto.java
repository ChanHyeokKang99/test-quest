package com.justteam.test_quest_api.api.user.dto;

import com.justteam.test_quest_api.api.user.entity.User;
import com.justteam.test_quest_api.jwt.hash.SecureHashUtils;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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

    @NotBlank(message = "프로필 이미지를 넣어주세요")
    private String profileImg;

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
