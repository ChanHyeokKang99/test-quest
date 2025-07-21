package com.justteam.test_quest_api.api.fcm.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class FCMService {

    /**
     * 단일 기기에 FCM 메시지 전송
     *
     * @param token 수신 기기의 FCM 토큰
     * @param title 알림 제목
     * @param body  알림 내용
     * @param data  추가 데이터 맵
     * @return 메시지 ID
     */
    public String sendMessageToToken(String token, String title, String body, Map<String, String> data) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)
                    .build();

            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info("메시지가 성공적으로 전송되었습니다. 응답: {}", response);
            return response;
        } catch (InterruptedException | ExecutionException e) {
            log.error("FCM 메시지 전송 중 오류 발생: {}", e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException("FCM 메시지 전송 실패", e);
        }
    }

    /**
     * 여러 기기에 FCM 메시지 전송
     *
     * @param tokens 수신 기기들의 FCM 토큰 목록
     * @param title  알림 제목
     * @param body   알림 내용
     * @param data   추가 데이터 맵
     * @return 메시지 전송 결과
     */
    public BatchResponse sendMessageToTokens(List<String> tokens, String title, String body, Map<String, String> data) {
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendMulticastAsync(message).get();
            log.info("메시지가 성공적으로 전송되었습니다. 성공: {}, 실패: {}", 
                    response.getSuccessCount(), response.getFailureCount());
            return response;
        } catch (InterruptedException | ExecutionException e) {
            log.error("FCM 멀티캐스트 메시지 전송 중 오류 발생: {}", e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException("FCM 멀티캐스트 메시지 전송 실패", e);
        }
    }

    /**
     * 특정 주제를 구독한 모든 기기에 FCM 메시지 전송
     *
     * @param topic 메시지를 보낼 주제
     * @param title 알림 제목
     * @param body  알림 내용
     * @param data  추가 데이터 맵
     * @return 메시지 ID
     */
    public String sendMessageToTopic(String topic, String title, String body, Map<String, String> data) {
        try {
            Message message = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)
                    .build();

            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            log.info("주제 {} 메시지가 성공적으로 전송되었습니다. 응답: {}", topic, response);
            return response;
        } catch (InterruptedException | ExecutionException e) {
            log.error("주제 {} FCM 메시지 전송 중 오류 발생: {}", topic, e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException("FCM 주제 메시지 전송 실패", e);
        }
    }

    /**
     * 기기를 특정 주제에 구독
     *
     * @param tokens 구독할 기기의 FCM 토큰 목록
     * @param topic  구독할 주제
     * @return 구독 결과
     */
    public TopicManagementResponse subscribeToTopic(List<String> tokens, String topic) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .subscribeToTopicAsync(tokens, topic).get();
            log.info("주제 {} 구독 성공: {}, 실패: {}", 
                    topic, tokens.size() - response.getFailureCount(), response.getFailureCount());
            return response;
        } catch (InterruptedException | ExecutionException e) {
            log.error("주제 {} 구독 중 오류 발생: {}", topic, e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException("주제 구독 실패", e);
        }
    }

    /**
     * 기기의 주제 구독 취소
     *
     * @param tokens 구독 취소할 기기의 FCM 토큰 목록
     * @param topic  구독 취소할 주제
     * @return 구독 취소 결과
     */
    public TopicManagementResponse unsubscribeFromTopic(List<String> tokens, String topic) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .unsubscribeFromTopicAsync(tokens, topic).get();
            log.info("주제 {} 구독 취소 성공: {}, 실패: {}", 
                    topic, tokens.size() - response.getFailureCount(), response.getFailureCount());
            return response;
        } catch (InterruptedException | ExecutionException e) {
            log.error("주제 {} 구독 취소 중 오류 발생: {}", topic, e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException("주제 구독 취소 실패", e);
        }
    }
} 