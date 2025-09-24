package com.justteam.test_quest_api.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor; // Add this to generate a constructor for JPA projection

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Important for JPA constructor expression in @Query
public class UserInfoDto {
    @Schema(example = "홍길동")
    private String name;

    @Schema(example = "user_1234")
    private String userId;

    @Schema(example = "https://cdn.example.com/profile.png")
    private String profileImg;

    @Schema(example = "길동")
    private String nickname;
}