package com.justteam.test_quest_api.common.exception;

public class NotFound extends ClientError {
    public NotFound(String message) {
        this.errorCode = "NotFound";
        this.errorMessage = message;
    }
}