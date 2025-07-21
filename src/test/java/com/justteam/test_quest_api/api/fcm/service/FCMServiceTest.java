package com.justteam.test_quest_api.api.fcm.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FCMServiceTest {

    @InjectMocks
    private FCMService fcmService;

    private Map<String, String> testData;

    @BeforeEach
    void setUp() {
        testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", "value2");
    }

    @Test
    @DisplayName("단일 기기에 메시지 전송 테스트")
    void testSendMessageToToken() throws ExecutionException, InterruptedException {
        // given
        String token = "test-token";
        String title = "테스트 제목";
        String body = "테스트 내용";
        String expectedMessageId = "message-id-123";

        try (MockedStatic<FirebaseMessaging> mockedStatic = Mockito.mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging firebaseMessaging = Mockito.mock(FirebaseMessaging.class);
            mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            
            @SuppressWarnings("unchecked")
            ApiFuture<String> future = Mockito.mock(ApiFuture.class);
            when(future.get()).thenReturn(expectedMessageId);
            when(firebaseMessaging.sendAsync(any(Message.class))).thenReturn(future);

            // when
            String result = fcmService.sendMessageToToken(token, title, body, testData);

            // then
            assertEquals(expectedMessageId, result);
        }
    }

    @Test
    @DisplayName("여러 기기에 메시지 전송 테스트")
    void testSendMessageToTokens() throws ExecutionException, InterruptedException {
        // given
        List<String> tokens = Arrays.asList("token1", "token2", "token3");
        String title = "테스트 제목";
        String body = "테스트 내용";
        BatchResponse expectedResponse = Mockito.mock(BatchResponse.class);
        when(expectedResponse.getSuccessCount()).thenReturn(3);
        when(expectedResponse.getFailureCount()).thenReturn(0);

        try (MockedStatic<FirebaseMessaging> mockedStatic = Mockito.mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging firebaseMessaging = Mockito.mock(FirebaseMessaging.class);
            mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            
            @SuppressWarnings("unchecked")
            ApiFuture<BatchResponse> future = Mockito.mock(ApiFuture.class);
            when(future.get()).thenReturn(expectedResponse);
            when(firebaseMessaging.sendMulticastAsync(any(MulticastMessage.class))).thenReturn(future);

            // when
            BatchResponse result = fcmService.sendMessageToTokens(tokens, title, body, testData);

            // then
            assertEquals(expectedResponse, result);
        }
    }

    @Test
    @DisplayName("주제 메시지 전송 테스트")
    void testSendMessageToTopic() throws ExecutionException, InterruptedException {
        // given
        String topic = "new_posts";
        String title = "테스트 제목";
        String body = "테스트 내용";
        String expectedMessageId = "topic-message-id-123";

        try (MockedStatic<FirebaseMessaging> mockedStatic = Mockito.mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging firebaseMessaging = Mockito.mock(FirebaseMessaging.class);
            mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            
            @SuppressWarnings("unchecked")
            ApiFuture<String> future = Mockito.mock(ApiFuture.class);
            when(future.get()).thenReturn(expectedMessageId);
            when(firebaseMessaging.sendAsync(any(Message.class))).thenReturn(future);

            // when
            String result = fcmService.sendMessageToTopic(topic, title, body, testData);

            // then
            assertEquals(expectedMessageId, result);
        }
    }

    @Test
    @DisplayName("메시지 전송 실패 시 예외 처리 테스트")
    void testSendMessageToTokenFailure() throws ExecutionException, InterruptedException {
        // given
        String token = "test-token";
        String title = "테스트 제목";
        String body = "테스트 내용";
        ExecutionException exception = new ExecutionException("메시지 전송 실패", new RuntimeException());

        try (MockedStatic<FirebaseMessaging> mockedStatic = Mockito.mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging firebaseMessaging = Mockito.mock(FirebaseMessaging.class);
            mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
            
            @SuppressWarnings("unchecked")
            ApiFuture<String> future = Mockito.mock(ApiFuture.class);
            when(future.get()).thenThrow(exception);
            when(firebaseMessaging.sendAsync(any(Message.class))).thenReturn(future);

            // when & then
            assertThrows(RuntimeException.class, () -> fcmService.sendMessageToToken(token, title, body, testData));
        }
    }
} 