package com.shophub.rest.config.filter;

import com.shophub.rest.config.CommonEnvConfig;
import com.shophub.rest.config.rest.ETokenType;
import com.shophub.rest.config.rest.RequestCtxDataDelivery;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CookiesSupportFilter extends BaseFilter {

    @Autowired
    public CookiesSupportFilter(RequestCtxDataDelivery reqCtxDelivery, CommonEnvConfig envConfig) {
        super(reqCtxDelivery, envConfig);
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getCookies() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Map<String, String> cookies = Arrays.stream(request.getCookies())
                .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
            super.reqCtxDelivery.getCookiesHolder().setJwtAccess(cookies.get(ETokenType.JWT_ACCESS.toString()));
            super.reqCtxDelivery.getCookiesHolder().setJwtRefresh(cookies.get(ETokenType.JWT_REFRESH.toString()));

            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            super.handleFilterException(response, ex);
        }
    }

}
