package com.shophub.rest.controller;

import com.shophub.rest.config.rest.SuccessCodes;
import com.shophub.rest.dto.request.AuthReq;
import com.shophub.rest.entity.rest.RestApiResponse;
import com.shophub.rest.service.auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.shophub.rest.util.contants.CCommon.API;

@RestController
@RequiredArgsConstructor
public class AuthController {
    AuthService authService;

    @PostMapping(API.PUBLIC + "/authenticate")
    public RestApiResponse<Void> authenticate(@RequestBody @Valid AuthReq request, HttpServletResponse response) {
        authService.authenticate(request, response);
        return RestApiResponse.fromSuccess(SuccessCodes.AUTH);
    }

    @PostMapping(API.SECURE + "/logout")
    public RestApiResponse<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return RestApiResponse.fromSuccess(SuccessCodes.LOGOUT);
    }

    @PostMapping(API.SECURE + "/refresh-token")
    public RestApiResponse<Void> refreshAccessToken(HttpServletResponse response) {
        authService.refreshAccessToken(response);
        return RestApiResponse.fromSuccess(SuccessCodes.UPDATE);
    }
}
