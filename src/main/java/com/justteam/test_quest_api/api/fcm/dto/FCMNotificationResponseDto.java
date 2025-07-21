package com.justteam.test_quest_api.api.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FCMNotificationResponseDto {
    private String messageId;
    private int successCount;
    private int failureCount;
    private String status;
    private String message;
} 