package com.justteam.test_quest_api.api.fcm.repository;

import com.justteam.test_quest_api.api.fcm.entity.FCMToken;
import com.justteam.test_quest_api.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
    
    /**
     * 사용자의 모든 활성 토큰 조회
     */
    List<FCMToken> findByUserAndIsActiveTrue(User user);
    
    /**
     * 특정 토큰 문자열로 토큰 조회
     */
    Optional<FCMToken> findByToken(String token);
    
    /**
     * 사용자와 기기 ID로 토큰 조회
     */
    Optional<FCMToken> findByUserAndDeviceId(User user, String deviceId);
    
    /**
     * 사용자의 모든 토큰 비활성화
     */
    @Modifying
    @Transactional
    @Query("UPDATE FCMToken t SET t.isActive = false WHERE t.user = :user")
    void deactivateAllUserTokens(User user);
    
    /**
     * 특정 토큰 비활성화
     */
    @Modifying
    @Transactional
    @Query("UPDATE FCMToken t SET t.isActive = false WHERE t.token = :token")
    void deactivateToken(String token);
} 