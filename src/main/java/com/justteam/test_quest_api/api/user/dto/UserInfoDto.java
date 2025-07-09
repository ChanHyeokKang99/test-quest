package com.justteam.test_quest_api.api.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor; // Add this to generate a constructor for JPA projection

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Important for JPA constructor expression in @Query
public class UserInfoDto {
    private String name;
    private String profileImg;
    private String nickname;
}