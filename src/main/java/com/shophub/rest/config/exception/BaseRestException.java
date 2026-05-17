package com.shophub.rest.config.exception;

import com.shophub.rest.config.rest.ErrorCodes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseRestException extends RuntimeException {
    private ErrorCodes errorCode;
    private Exception wrappedExc;

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

    public BaseRestException(Exception e) {
        super(e);
        this.wrappedExc = e;
    }

    public BaseRestException(ErrorCodes errorCodes, Exception e) {
        super(e);
        this.wrappedExc = e;
        this.errorCode = errorCodes;
    }
}
