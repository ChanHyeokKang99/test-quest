package com.justteam.test_quest_api.common.exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BadParameter extends ClientError {
    public BadParameter(String message) {
        this.errorCode = "400";
        this.errorMessage = message;
    }
}