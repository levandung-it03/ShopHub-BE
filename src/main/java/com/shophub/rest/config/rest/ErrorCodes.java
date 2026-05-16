package com.shophub.rest.config.rest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCodes {
    UNAWARE_ERR("Unaware exc thrown!", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TOKEN("JWT is invalid!", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_USER("User accessing denied!", HttpStatus.FORBIDDEN),
    ;
    String msg;
    HttpStatus httpStatus;
}
