package com.justteam.test_quest_api.api.fcm.entity;

import com.justteam.test_quest_api.api.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fcm_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FCMToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false, length = 500)
    private String token;
    
    @Column(name = "device_id", length = 255)
    private String deviceId;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
} 