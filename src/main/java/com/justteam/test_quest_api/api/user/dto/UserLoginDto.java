package com.justteam.test_quest_api.api.user.dto;

import com.justteam.test_quest_api.api.user.entity.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginDto {
    
    // @NotBlank(message = "이메일를 입력해주세요")
    private String email;

    // @NotBlank(message = "비밀번호를 입력하세요")
    private String password;

    public User toEntity() {
        User user = new User();
        user.setEmail(this.email);
        user.setPassword(this.password);
        return user;
    }
}
