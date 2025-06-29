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
import org.springframework.util.StringUtils; // <-- StringUtils 임포트 추가

import java.io.ByteArrayInputStream; // <-- ByteArrayInputStream 임포트 추가
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets; // <-- StandardCharsets 임포트 추가

@Configuration
@Slf4j
public class FireBaseConfig {

    @Value("${firebase.sdk.path:classpath:serviceAccountKey.json}")
    private String firebaseSdkPath;

    @Value("${firebase.sdk.content:}") // 기본값은 빈 문자열
    private String firebaseSdkContent;

    @Value("${firebase.storage.bucket}")
    private String firebaseBucket;

    @PostConstruct
    public void initialize() throws IOException {
        try {
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder();
            GoogleCredentials credentials;

            if (StringUtils.hasText(firebaseSdkContent)) {
                InputStream serviceAccountStream = new ByteArrayInputStream(firebaseSdkContent.getBytes(StandardCharsets.UTF_8));
                credentials = GoogleCredentials.fromStream(serviceAccountStream);
                log.info("Firebase initialization using service account JSON content from environment variable.");
            }
            else {
                InputStream serviceAccount = new ClassPathResource(firebaseSdkPath.substring("classpath:".length())).getInputStream();
                credentials = GoogleCredentials.fromStream(serviceAccount);
                log.info("Firebase initialization using service account key from classpath: {}", firebaseSdkPath);
            }

            optionsBuilder.setCredentials(credentials);
            optionsBuilder.setStorageBucket(firebaseBucket); // 버킷 설정
            FirebaseOptions options = optionsBuilder.build();

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
    public Bucket firebaseBucket() {
        // FirebaseApp이 초기화된 후에 호출되어야 함 (PostConstruct가 먼저 실행됨)
        return StorageClient.getInstance().bucket();
    }
}