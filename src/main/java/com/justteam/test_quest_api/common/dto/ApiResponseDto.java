package com.justteam.test_quest_api.common.dto;

import lombok.Getter;

@Getter
public class ApiResponseDto<T> {
    private String code;
    private String message;
    private T data;

    private ApiResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private ApiResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponseDto<T> createOk(T data) {
        return new ApiResponseDto("200", "요청이 성공하였습니다.", data);
    }

    public static <T>ApiResponseDto<T> defaultOk() {
        return ApiResponseDto.createOk(null);
    }

    public static <T>ApiResponseDto<T> createError(String code, String message) {
        return new ApiResponseDto(code, message, null);
    }
}
