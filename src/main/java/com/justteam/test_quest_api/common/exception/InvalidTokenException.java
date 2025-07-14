package com.justteam.test_quest_api.common.exception;

public class InvalidTokenException extends RuntimeException {
    private String errorCode;
    private String errorMessage;

    public InvalidTokenException(String message) {
        super(message);
        this.errorMessage = message;
        this.errorCode = "401";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}