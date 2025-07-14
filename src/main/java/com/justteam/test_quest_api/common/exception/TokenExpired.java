package com.justteam.test_quest_api.common.exception;

public class TokenExpired extends ClientError {
    public TokenExpired(String message) {
        this.errorCode = "TokenExpired";  // 에러 코드 정의
        this.errorMessage = message;      // 에러 메시지 설정
    }
}