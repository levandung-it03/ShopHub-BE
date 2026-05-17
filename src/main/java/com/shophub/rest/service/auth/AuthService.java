package com.shophub.rest.service.auth;

import com.shophub.rest.config.CommonEnvConfig;
import com.shophub.rest.config.exception.RestServiceException;
import com.shophub.rest.config.rest.ETokenType;
import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.config.rest.RequestCtxDataDelivery;
import com.shophub.rest.dto.request.AuthReq;
import com.shophub.rest.entity.auth.Account;
import com.shophub.rest.entity.auth.UserProfile;
import com.shophub.rest.entity.enums.EProvider;
import com.shophub.rest.entity.rest.JwtInfo;
import com.shophub.rest.service.AccountService;
import com.shophub.rest.service.UserProfileService;
import com.shophub.rest.service.jwt.PrivateJwtService;
import com.shophub.rest.service.jwt.PublicJwtService;
import com.shophub.rest.util.contants.CCommon;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService {
    AccountService accountService;
    UserProfileService userProfileService;
    PasswordEncoder userPasswordEncoder;
    PrivateJwtService privateJwtSvc;
    PublicJwtService publicJwtSvc;
    CommonEnvConfig env;
    RequestCtxDataDelivery reqCtxData;

    @Override
    public void authenticate(AuthReq request, HttpServletResponse response) {
        Account account = accountService.findByEmail(request.getEmail())
            .orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_CREDENTIALS));

        if (!userPasswordEncoder.matches(request.getPassword(), account.getPassword()))
            throw new RestServiceException(ErrorCodes.INVALID_CREDENTIALS);

        var userProfile = userProfileService.findByAccountId(account.getId())
            .orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID));

        this.createAndTransferJwtAccessViaCookies(response, userProfile);
        this.createAndTransferJwtRefreshViaCookies(response, userProfile);
    }

    public void createAndTransferJwtRefreshViaCookies(HttpServletResponse response, UserProfile userInfo) throws RestServiceException {
        JwtInfo refreshTknInfo = JwtInfo.builder()
            .jti(UUID.randomUUID().toString())
            .userId(userInfo.getId())
            .fullName(userInfo.getFullName())
            .type(ETokenType.JWT_REFRESH)
            .build();

        try {
            var refreshTknCookie = ResponseCookie
                .from(ETokenType.JWT_REFRESH.toString(), privateJwtSvc.generate(refreshTknInfo))
                .httpOnly(true)
                .sameSite(env.SAME_SITE_COOKIES())
                .domain(env.SVC_DOMAIN())
                .secure(env.IS_SECURING_COOKIES())
                .path(CCommon.API.REFRESH_TOKEN)
                .maxAge(env.REFRESH_EXPIRY())
                .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTknCookie.toString());
        } catch (Exception e) {
            throw new RestServiceException(ErrorCodes.INVALID_CREDENTIALS, e);
        }
    }

    public void createAndTransferJwtAccessViaCookies(HttpServletResponse response, UserProfile userInfo) throws RestServiceException {
        JwtInfo accessTknInfo = JwtInfo.builder()
            .jti(UUID.randomUUID().toString())
            .userId(userInfo.getId())
            .fullName(userInfo.getFullName())
            .role(userInfo.getAccount().getAuthority().getAuthority())
            .provider(EProvider.LOCAL)
            .type(ETokenType.JWT_ACCESS)
            .build();

        try {
            var accessTknCookie = ResponseCookie
                .from(ETokenType.JWT_ACCESS.toString(), privateJwtSvc.generate(accessTknInfo))
                .httpOnly(true)
                .sameSite(env.SAME_SITE_COOKIES())
                .domain(env.SVC_DOMAIN())
                .secure(env.IS_SECURING_COOKIES())
                .path(CCommon.API.SECURE)
                .maxAge(env.ACCESS_EXPIRY())
                .build();
            response.addHeader(HttpHeaders.SET_COOKIE, accessTknCookie.toString());
        } catch (Exception e) {
            throw new RestServiceException(ErrorCodes.INVALID_CREDENTIALS, e);
        }
    }

    @Override
    public void logout(HttpServletResponse response) {
        String jwtAccessStr = reqCtxData.getCookiesHolder().getJwtAccess();
        String jwtRefreshStr = reqCtxData.getCookiesHolder().getJwtRefresh();

        JwtInfo jwtAccess = publicJwtSvc.read(jwtAccessStr);
        JwtInfo jwtRefresh = publicJwtSvc.read(jwtRefreshStr);

        var jwtAccessStillValid = Instant.now().isBefore(jwtAccess.getExpiresAt());
        if (jwtAccessStillValid)   publicJwtSvc.killAccessToken(jwtAccess.getJti());

        var jwtRefreshStillValid = Instant.now().isBefore(jwtRefresh.getExpiresAt());
        if (jwtRefreshStillValid)  publicJwtSvc.killRefreshToken(jwtRefresh.getJti());

        var invalidatedAccessCookie = ResponseCookie
            .from(ETokenType.JWT_ACCESS.toString(), CCommon.Symbols.EMPTY)
            .httpOnly(true)
            .sameSite(env.SAME_SITE_COOKIES())
            .domain(env.SVC_DOMAIN())
            .secure(env.IS_SECURING_COOKIES())
            .path(CCommon.API.SECURE)
            .maxAge(0)
            .build();

        var invalidatedRefreshCookie = ResponseCookie
            .from(ETokenType.JWT_REFRESH.toString(), CCommon.Symbols.EMPTY)
            .httpOnly(true)
            .sameSite(env.SAME_SITE_COOKIES())
            .domain(env.SVC_DOMAIN())
            .secure(env.IS_SECURING_COOKIES())
            .path(CCommon.API.REFRESH_TOKEN)
            .maxAge(0)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, invalidatedAccessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, invalidatedRefreshCookie.toString());
    }

    @Override
    public void refreshAccessToken(HttpServletResponse response) throws RestServiceException {
        JwtInfo jwtRefresh = reqCtxData.getAuthzedTokenInfo();

        var jwtRefreshStillValid = Instant.now().isBefore(jwtRefresh.getExpiresAt());
        if (!jwtRefreshStillValid)  throw new RestServiceException(ErrorCodes.INVALID_CREDENTIALS);

        var shortUserProfile = UserProfile.builder()
            .id(jwtRefresh.getUserId())
            .fullName(jwtRefresh.getFullName())
            .build();
        this.createAndTransferJwtAccessViaCookies(response, shortUserProfile);
    }
}
