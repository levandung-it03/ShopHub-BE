package com.shophub.rest.entity.rest;

import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.config.rest.SuccessCodes;
import com.shophub.rest.util.contants.CCommon;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.ser.InstantSerializer;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestApiResponse<T> extends ResponseEntity<RestApiResponse.ResponseBody<T>> {

    public RestApiResponse(HttpStatusCode httpStatus) {
        super(httpStatus);
    }

    public RestApiResponse(HttpStatusCode status, ResponseBody body) {
        super(body, status);
    }

    public static RestApiResponse<Void> fromSuccess(SuccessCodes sucCode) {
        return new RestApiResponse<>(sucCode.getHttpStatus(),
            ResponseBody.<Void>builder()
                .status(sucCode.getHttpStatus().value())
                .code(sucCode.toString())
                .msg(sucCode.getMsg())
                .time(Instant.now())
                .build());
    }

    public static <T> RestApiResponse<T> fromSuccess(SuccessCodes sucCode, T data) {
        return new RestApiResponse<>(sucCode.getHttpStatus(),
            ResponseBody.<T>builder()
                .status(sucCode.getHttpStatus().value())
                .code(sucCode.toString())
                .msg(sucCode.getMsg())
                .time(Instant.now())
                .data(data)
                .build());
    }

    public static RestApiResponse<Void> fromErr(ErrorCodes errCode, String replacedMsg) {
        return new RestApiResponse<>(errCode.getHttpStatus(),
            ResponseBody.<Void>builder()
                .status(errCode.getHttpStatus().value())
                .code(errCode.toString())
                .msg(replacedMsg)
                .time(Instant.now())
                .build());
    }

    public static RestApiResponse<Void> fromErr(ErrorCodes errCode) {
        return new RestApiResponse<>(errCode.getHttpStatus(),
            ResponseBody.<Void>builder()
                .status(errCode.getHttpStatus().value())
                .code(errCode.toString())
                .msg(errCode.getMsg())
                .time(Instant.now())
                .build());
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseBody<T> {
        Integer status; // duplicated one (nested level) with default ResponseEntity.status
        String code;
        String msg;
        @JsonSerialize(using = InstantSerializer.class)
        Instant time;
        T data;
    }
}
