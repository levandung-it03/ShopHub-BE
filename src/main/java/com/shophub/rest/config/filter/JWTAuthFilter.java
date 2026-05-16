package com.shophub.rest.config.filter;

import com.shophub.rest.config.CommonEnvConfig;
import com.shophub.rest.config.exception.FilterHandlingException;
import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.config.rest.RequestCtxDataDelivery;
import com.shophub.rest.entity.rest.JwtInfo;
import com.shophub.rest.service.auth.JwtService;
import com.shophub.rest.util.contants.CCommon;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class JWTAuthFilter extends BaseFilter {
    private final String[] securedUrls = new String[] {
        CCommon.API.SECURE
    };
    private final String[] verifiedUrlsByRefreshTkn = new String[] {
        CCommon.API.REFRESH_TOKEN
    };
    private final JwtService jwtService;

    @Autowired
    public JWTAuthFilter(RequestCtxDataDelivery reqCtxDelivery, CommonEnvConfig envConfig, JwtService jwtService) {
        super(reqCtxDelivery, envConfig);
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            var url = request.getRequestURL().toString();
            var isSecuredUrl = Arrays.stream(securedUrls).anyMatch(url::contains);
            if (!isSecuredUrl) {
                filterChain.doFilter(request, response);
                return;
            }

            var token = this.getToken(request);
            if (!jwtService.checkIsValid(token))
                throw new FilterHandlingException(ErrorCodes.INVALID_TOKEN);

            var isAuthorizedTokenBySystem = jwtService.isAuthorizedToken(token);
            if (!isAuthorizedTokenBySystem)
                throw new FilterHandlingException(ErrorCodes.INVALID_TOKEN);

            var jwtInfo = jwtService.read(token);
            this.registerSecurityContext(request, jwtInfo);
            super.reqCtxDelivery.setJwtInfo(jwtInfo);

            filterChain.doFilter(request, response);
        } catch (FilterHandlingException e) {
            super.handleFilterException(response, e);
        }
    }

    private String getToken(HttpServletRequest request) {
        var url = request.getRequestURL().toString();
        var isVerifyingWithRefreshToken = Arrays.stream(verifiedUrlsByRefreshTkn).anyMatch(url::contains);
        return isVerifyingWithRefreshToken
            ? super.reqCtxDelivery.getCookiesHolder().getJwtAccess()
            : super.reqCtxDelivery.getCookiesHolder().getJwtRefresh();
    }

    private void registerSecurityContext(HttpServletRequest request, JwtInfo jwtClaims) {
        var authoritiesProvider = new UsernamePasswordAuthenticationToken(
            jwtClaims.getUserId(), null,
            this.extractRoles(jwtClaims));
        authoritiesProvider.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authoritiesProvider);
    }

    private Collection<? extends GrantedAuthority> extractRoles(JwtInfo jwtClaims) {
        return List.of(new GrantedAuthority() {
            @Override
            public @Nullable String getAuthority() {
                return jwtClaims.getRole().getName();
            }
        });
    }

}
