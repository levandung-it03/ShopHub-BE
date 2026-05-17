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
    USER_NOTFOUND("User not found!", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("Email or password is incorrected", HttpStatus.BAD_REQUEST),
    INVALID_ID("Invalid id on entity ${}", HttpStatus.BAD_REQUEST),
    ;
    String msg;
    HttpStatus httpStatus;
}
