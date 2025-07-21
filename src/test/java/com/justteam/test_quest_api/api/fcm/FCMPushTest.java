package com.justteam.test_quest_api.api.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@ActiveProfiles("test")
public class FCMPushTest {

    @Test
    public void testSendPushNotification() throws ExecutionException, InterruptedException {
        // FCM 토큰 (실제 테스트 시 유효한 토큰으로 변경 필요)
        String token = "YOUR_FCM_TOKEN";
        
        // 알림 데이터
        Map<String, String> data = new HashMap<>();
        data.put("title", "테스트 알림");
        data.put("body", "이것은 테스트 알림입니다.");
        data.put("click_action", "OPEN_MAIN");
        
        // 메시지 생성
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle("테스트 알림")
                        .setBody("이것은 테스트 알림입니다.")
                        .build())
                .putAllData(data)
                .build();
        
        // 메시지 전송
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        System.out.println("Successfully sent message: " + response);
    }
    
    @Test
    public void testSendTopicPushNotification() throws ExecutionException, InterruptedException {
        // 주제 (topic)
        String topic = "new_posts";
        
        // 알림 데이터
        Map<String, String> data = new HashMap<>();
        data.put("title", "새 글 알림");
        data.put("body", "새로운 게시글이 등록되었습니다.");
        data.put("click_action", "OPEN_POST_LIST");
        
        // 메시지 생성
        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setTitle("새 글 알림")
                        .setBody("새로운 게시글이 등록되었습니다.")
                        .build())
                .putAllData(data)
                .build();
        
        // 메시지 전송
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        System.out.println("Successfully sent topic message: " + response);
    }
} 