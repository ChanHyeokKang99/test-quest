package com.justteam.test_quest_api.api.fcm.service;

import com.justteam.test_quest_api.api.fcm.entity.FCMToken;
import com.justteam.test_quest_api.api.fcm.repository.FCMTokenRepository;
import com.justteam.test_quest_api.api.user.entity.User;
import com.justteam.test_quest_api.api.user.repository.UserRepository;
import com.justteam.test_quest_api.common.exception.NotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMTokenService {

    private final FCMTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 FCM 토큰 등록/갱신
     *
     * @param userId   사용자 ID
     * @param token    FCM 토큰
     * @param deviceId 기기 ID (선택적)
     * @return 저장된 토큰 엔티티
     */
    @Transactional
    public FCMToken registerToken(String userId, String token, String deviceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFound("사용자를 찾을 수 없습니다. ID: " + userId));

        // 기기 ID가 있으면 해당 기기의 기존 토큰을 찾아서 업데이트
        if (deviceId != null && !deviceId.isEmpty()) {
            FCMToken existingToken = fcmTokenRepository.findByUserAndDeviceId(user, deviceId)
                    .orElse(null);

            if (existingToken != null) {
                existingToken.setToken(token);
                existingToken.setUpdatedAt(LocalDateTime.now());
                existingToken.setActive(true);
                return fcmTokenRepository.save(existingToken);
            }
        }

        // 기존 토큰이 없으면 새로 생성
        FCMToken fcmToken = FCMToken.builder()
                .user(user)
                .token(token)
                .deviceId(deviceId)
                .build();

        return fcmTokenRepository.save(fcmToken);
    }

    /**
     * 토큰 비활성화 (로그아웃 시 호출)
     *
     * @param token FCM 토큰
     */
    @Transactional
    public void deactivateToken(String token) {
        fcmTokenRepository.deactivateToken(token);
        log.info("토큰이 비활성화되었습니다: {}", token);
    }

    /**
     * 사용자의 모든 토큰 비활성화
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void deactivateAllUserTokens(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFound("사용자를 찾을 수 없습니다. ID: " + userId));
        
        fcmTokenRepository.deactivateAllUserTokens(user);
        log.info("사용자 ID {}의 모든 토큰이 비활성화되었습니다", userId);
    }

    /**
     * 사용자의 활성 토큰 목록 조회
     *
     * @param userId 사용자 ID
     * @return 활성 토큰 문자열 목록
     */
    @Transactional(readOnly = true)
    public List<String> getUserActiveTokens(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFound("사용자를 찾을 수 없습니다. ID: " + userId));
        
        return fcmTokenRepository.findByUserAndIsActiveTrue(user).stream()
                .map(FCMToken::getToken)
                .collect(Collectors.toList());
    }
} 