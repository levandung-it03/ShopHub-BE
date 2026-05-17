package com.shophub.rest.service.jwt;

import com.shophub.rest.config.exception.BaseRestException;
import com.shophub.rest.config.rest.ErrorCodes;

public class JwtServiceException extends BaseRestException {

    public JwtServiceException(String message) {
        super(message);
    }

    public JwtServiceException(ErrorCodes errorCodes) {
        super(errorCodes);
    }

    public JwtServiceException(ErrorCodes errorCodes, String msg) {
        super(errorCodes, msg);
    }

    public JwtServiceException(Exception e) {
        super(e);
    }

    public JwtServiceException(ErrorCodes errorCodes, Exception e) {
        super(errorCodes, e);
    }
}
