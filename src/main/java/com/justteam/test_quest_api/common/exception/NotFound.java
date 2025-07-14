package com.justteam.test_quest_api.common.exception;

public class NotFound extends ClientError {
    public NotFound(String message) {
        this.errorCode = "404";
        this.errorMessage = message;
    }
}