package com.justteam.test_quest_api.api.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FCMNotificationRequestDto {
    private String token;             // 단일 기기 토큰
    private List<String> tokens;      // 다중 기기 토큰 목록
    private String topic;             // 주제
    private String title;             // 알림 제목
    private String body;              // 알림 내용
    
    @Builder.Default
    private Map<String, String> data = new HashMap<>();  // 추가 데이터
} 