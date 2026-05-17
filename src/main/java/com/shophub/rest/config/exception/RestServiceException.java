package com.shophub.rest.config.exception;

import com.shophub.rest.config.rest.ErrorCodes;

public class RestServiceException extends BaseRestException {

    public RestServiceException(String message) {
        super(message);
    }

    public RestServiceException(ErrorCodes errorCodes) {
        super(errorCodes);
    }

    public RestServiceException(ErrorCodes errorCodes, String msg) {
        super(errorCodes, msg);
    }

    public RestServiceException(Exception e) {
        super(e);
    }

    public RestServiceException(ErrorCodes errorCodes, Exception e) {
        super(errorCodes, e);
    }
}
