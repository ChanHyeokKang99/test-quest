package com.justteam.test_quest_api.advice;

import com.justteam.test_quest_api.advice.parameter.ParameterErrorDto;
import com.justteam.test_quest_api.common.dto.ApiResponseDto;
import com.justteam.test_quest_api.common.exception.BadParameter;
import com.justteam.test_quest_api.common.exception.ClientError;
import com.justteam.test_quest_api.common.exception.InvalidTokenException;
import com.justteam.test_quest_api.common.exception.NotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@Order(value = 1)
@RestControllerAdvice
public class ApiCommonAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadParameter.class})
    public ApiResponseDto<String> handleBadParameter(BadParameter e) {
        return ApiResponseDto.createError(
                e.getErrorCode(),
                e.getErrorMessage()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFound.class})
    public ApiResponseDto<String> handleNotFound(NotFound e) {
        return ApiResponseDto.createError(
                e.getErrorCode(),
                e.getErrorMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ClientError.class})
    public ApiResponseDto<String> handleClientError(ClientError e) {
        return ApiResponseDto.createError(
                e.getErrorCode(),
                e.getErrorMessage()
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({InvalidTokenException.class})
    public ApiResponseDto<String> handleInvalidToken(InvalidTokenException e) {
        return ApiResponseDto.createError(
                "401",
                e.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoResourceFoundException.class})
    public ApiResponseDto<String> handleNoResourceFoundException(NoResourceFoundException e) {
        return ApiResponseDto.createError(
                "NoResource",
                "리소스를 찾을 수 없습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ApiResponseDto<ParameterErrorDto.FieldList> handleArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        ParameterErrorDto.FieldList fieldList = ParameterErrorDto.FieldList.of(result);

        String errorMessage = fieldList.getErrorMessage();
        return ApiResponseDto.createError("ParameterNotValid", errorMessage, fieldList);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ApiResponseDto<String > handleException(Exception e) {
        log.info(e.getMessage());
        return ApiResponseDto.createError(
                "ServerError",
                "서버 에러입니다.");
    }

}