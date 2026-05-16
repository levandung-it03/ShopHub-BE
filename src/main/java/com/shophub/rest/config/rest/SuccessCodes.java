package com.shophub.rest.config.rest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SuccessCodes {
    GET("Successfully got data!", HttpStatus.OK),
    CREATE("Successfully created data!", HttpStatus.CREATED),
    UPDATE("Successfully updated data!", HttpStatus.CREATED),
    DELETE("Successfully deleted data!", HttpStatus.NO_CONTENT),
    AUTH("Authenticated successfully!", HttpStatus.OK),
    ;
    String msg;
    HttpStatus httpStatus;
},

