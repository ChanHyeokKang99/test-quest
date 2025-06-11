package com.justteam.test_quest_api.jwt.hash;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecureHashUtils {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();    
    public static String hash(String message) {
            return encoder.encode(message);
    }

    public static boolean matches(String message, String hashedMessage) {
        return encoder.matches(message, hashedMessage);
    }
}