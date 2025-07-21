package com.justteam.test_quest_api.api.fcm.controller;

import com.google.firebase.messaging.BatchResponse;
import com.justteam.test_quest_api.api.fcm.dto.FCMNotificationRequestDto;
import com.justteam.test_quest_api.api.fcm.dto.FCMNotificationResponseDto;
import com.justteam.test_quest_api.api.fcm.dto.FCMTokenRequestDto;
import com.justteam.test_quest_api.api.fcm.service.FCMService;
import com.justteam.test_quest_api.api.fcm.service.FCMTokenService;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import com.justteam.test_quest_api.jwt.authentication.JwtAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fcm")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "FCM", description = "FCM 알림 관련 API")
@SecurityRequirement(name = "BearerAuth")
public class FCMController {

    private final FCMService fcmService;
    private final FCMTokenService fcmTokenService;

    @PostMapping("/token")
    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 등록합니다.")
    public ResponseEntity<ApiResponseDto<Void>> registerToken(
            @AuthenticationPrincipal JwtAuthentication auth,
            @Valid @RequestBody FCMTokenRequestDto requestDto) {
        
        fcmTokenService.registerToken(auth.getPrincipal().getUserId(), requestDto.getToken(), requestDto.getDeviceId());
        
        return ResponseEntity.ok(ApiResponseDto.defaultOk());
    }

    @DeleteMapping("/token")
    @Operation(summary = "FCM 토큰 삭제", description = "사용자의 FCM 토큰을 비활성화합니다.")
    public ResponseEntity<ApiResponseDto<Void>> deactivateToken(
            @RequestParam String token) {
        
        fcmTokenService.deactivateToken(token);
        
        return ResponseEntity.ok(ApiResponseDto.defaultOk());
    }

    @PostMapping("/send")
    @Operation(summary = "FCM 알림 전송", description = "특정 사용자에게 FCM 알림을 전송합니다.")
    public ResponseEntity<ApiResponseDto<FCMNotificationResponseDto>> sendNotification(
            @Valid @RequestBody FCMNotificationRequestDto requestDto) {
        
        FCMNotificationResponseDto responseDto;
        
        try {
            // 토큰이 있으면 단일 기기에 전송
            if (requestDto.getToken() != null && !requestDto.getToken().isEmpty()) {
                String messageId = fcmService.sendMessageToToken(
                        requestDto.getToken(),
                        requestDto.getTitle(),
                        requestDto.getBody(),
                        requestDto.getData());
                
                responseDto = FCMNotificationResponseDto.builder()
                        .messageId(messageId)
                        .successCount(1)
                        .failureCount(0)
                        .status("success")
                        .message("알림이 성공적으로 전송되었습니다.")
                        .build();
            }
            // 토큰 목록이 있으면 여러 기기에 전송
            else if (requestDto.getTokens() != null && !requestDto.getTokens().isEmpty()) {
                BatchResponse batchResponse = fcmService.sendMessageToTokens(
                        requestDto.getTokens(),
                        requestDto.getTitle(),
                        requestDto.getBody(),
                        requestDto.getData());
                
                responseDto = FCMNotificationResponseDto.builder()
                        .successCount(batchResponse.getSuccessCount())
                        .failureCount(batchResponse.getFailureCount())
                        .status(batchResponse.getFailureCount() == 0 ? "success" : "partial")
                        .message(String.format("알림이 전송되었습니다. 성공: %d, 실패: %d", 
                                batchResponse.getSuccessCount(), batchResponse.getFailureCount()))
                        .build();
            }
            // 주제가 있으면 주제 구독 기기에 전송
            else if (requestDto.getTopic() != null && !requestDto.getTopic().isEmpty()) {
                String messageId = fcmService.sendMessageToTopic(
                        requestDto.getTopic(),
                        requestDto.getTitle(),
                        requestDto.getBody(),
                        requestDto.getData());
                
                responseDto = FCMNotificationResponseDto.builder()
                        .messageId(messageId)
                        .status("success")
                        .message("주제 알림이 성공적으로 전송되었습니다.")
                        .build();
            }
            else {
                return ResponseEntity.badRequest().body(
                        ApiResponseDto.createError("400", "토큰, 토큰 목록 또는 주제 중 하나는 필수입니다.")
                );
            }
            
            return ResponseEntity.ok(ApiResponseDto.createOk(responseDto));
            
        } catch (Exception e) {
            log.error("알림 전송 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.createError("500", "알림 전송 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/send/user/{userId}")
    @Operation(summary = "사용자에게 FCM 알림 전송", description = "특정 사용자의 모든 기기에 FCM 알림을 전송합니다.")
    public ResponseEntity<ApiResponseDto<FCMNotificationResponseDto>> sendNotificationToUser(
            @PathVariable String userId,
            @Valid @RequestBody FCMNotificationRequestDto requestDto) {
        
        try {
            List<String> userTokens = fcmTokenService.getUserActiveTokens(userId);
            
            if (userTokens.isEmpty()) {
                FCMNotificationResponseDto emptyResponse = FCMNotificationResponseDto.builder()
                        .successCount(0)
                        .failureCount(0)
                        .status("no_tokens")
                        .message("등록된 활성 토큰 없음")
                        .build();
                
                return ResponseEntity.ok(ApiResponseDto.createOk(emptyResponse));
            }
            
            BatchResponse batchResponse = fcmService.sendMessageToTokens(
                    userTokens,
                    requestDto.getTitle(),
                    requestDto.getBody(),
                    requestDto.getData());
            
            FCMNotificationResponseDto responseDto = FCMNotificationResponseDto.builder()
                    .successCount(batchResponse.getSuccessCount())
                    .failureCount(batchResponse.getFailureCount())
                    .status(batchResponse.getFailureCount() == 0 ? "success" : "partial")
                    .message(String.format("알림이 전송되었습니다. 성공: %d, 실패: %d", 
                            batchResponse.getSuccessCount(), batchResponse.getFailureCount()))
                    .build();
            
            return ResponseEntity.ok(ApiResponseDto.createOk(responseDto));
            
        } catch (Exception e) {
            log.error("사용자 알림 전송 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.createError("500", "알림 전송 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/topic/subscribe")
    @Operation(summary = "주제 구독", description = "사용자의 기기를 특정 주제에 구독합니다.")
    public ResponseEntity<ApiResponseDto<Void>> subscribeToTopic(
            @AuthenticationPrincipal JwtAuthentication auth,
            @RequestParam String topic) {
        
        List<String> userTokens = fcmTokenService.getUserActiveTokens(auth.getPrincipal().getUserId());
        
        if (userTokens.isEmpty()) {
            return ResponseEntity.ok(ApiResponseDto.defaultOk());
        }
        
        fcmService.subscribeToTopic(userTokens, topic);
        
        return ResponseEntity.ok(ApiResponseDto.defaultOk());
    }

    @PostMapping("/topic/unsubscribe")
    @Operation(summary = "주제 구독 취소", description = "사용자의 기기를 특정 주제에서 구독 취소합니다.")
    public ResponseEntity<ApiResponseDto<Void>> unsubscribeFromTopic(
            @AuthenticationPrincipal JwtAuthentication auth,
            @RequestParam String topic) {
        
        List<String> userTokens = fcmTokenService.getUserActiveTokens(auth.getPrincipal().getUserId());
        
        if (userTokens.isEmpty()) {
            return ResponseEntity.ok(ApiResponseDto.defaultOk());
        }
        
        fcmService.unsubscribeFromTopic(userTokens, topic);
        
        return ResponseEntity.ok(ApiResponseDto.defaultOk());
    }
} 