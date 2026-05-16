package com.shophub.rest.config.exception;

import com.shophub.rest.config.rest.ErrorCodes;

public class FilterHandlingException extends BaseRestException {

    public FilterHandlingException(String message) {
        super(message);
    }

    public FilterHandlingException(ErrorCodes errorCodes) {
        super(errorCodes);
    }

    public FilterHandlingException(ErrorCodes errorCodes, String msg) {
        super(errorCodes, msg);
    }
}
