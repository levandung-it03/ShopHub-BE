package com.shophub.rest.service.auth;

import com.shophub.rest.config.exception.RestServiceException;
import com.shophub.rest.dto.request.AuthReq;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
    void authenticate(AuthReq request, HttpServletResponse response);

    void logout(HttpServletResponse response);

    void refreshAccessToken(HttpServletResponse response) throws RestServiceException;
}
