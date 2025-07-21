package com.justteam.test_quest_api.api.fcm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FCMTokenRequestDto {
    
    @NotBlank(message = "FCM 토큰은 필수입니다.")
    private String token;
    
    private String deviceId;
} 