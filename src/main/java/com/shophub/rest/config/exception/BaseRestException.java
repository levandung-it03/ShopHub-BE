package com.shophub.rest.config.exception;

import com.shophub.rest.config.rest.ErrorCodes;
import lombok.Getter;
import lombok.Setter;

public class BaseRestException extends RuntimeException {
    @Getter
    @Setter
    private ErrorCodes errorCode;

    public BaseRestException(String message) {
        super(message);
    }

    public BaseRestException(ErrorCodes errorCodes) {
        super(errorCodes.getMsg());
        this.errorCode = errorCodes;
    }

    public BaseRestException(ErrorCodes errorCodes, String msg) {
        super(msg);
        this.errorCode = errorCodes;
    }
}
