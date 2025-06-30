package com.justteam.test_quest_api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class FireBaseConfig {
    @Value("${firebase.sdk.json}")
    private String firebaseSdkJson;

    @Value("${firebase.storage.bucket}")
    private String firebaseBucket;

    @PostConstruct
    public void initialize() throws IOException {
        try {
            InputStream serviceAccount = new ByteArrayInputStream(firebaseSdkJson.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(firebaseBucket) // 버킷 설정
                    .build();

            if (FirebaseApp.getApps().isEmpty()) { // 이미 초기화된 앱이 없는 경우에만 초기화
                FirebaseApp.initializeApp(options);
                log.info("Firebase has been initialized successfully.");
            } else {
                log.info("Firebase already initialized.");
            }

        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
            throw new IOException("Failed to initialize Firebase", e);
        }
    }

    @Bean
    // Firebase Storage Bucket 인스턴스를 빈으로 등록하여 필요한 곳에서 주입받아 사용할 수 있게 함
    public Bucket getFirebaseBucket() {
        return StorageClient.getInstance().bucket();
    }
}
