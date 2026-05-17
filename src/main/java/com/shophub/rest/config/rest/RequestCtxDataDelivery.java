package com.shophub.rest.config.rest;

import com.shophub.rest.entity.rest.CookiesHolder;
import com.shophub.rest.entity.rest.JwtInfo;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
@Getter
public class RequestCtxDataDelivery {
    private CookiesHolder cookiesHolder;
    @Setter
    private JwtInfo authzedTokenInfo;

    public RequestCtxDataDelivery() {
        this.cookiesHolder = new CookiesHolder();
    }

    @PreDestroy
    public void onDestroy() {
        cookiesHolder = new CookiesHolder();
        setAuthzedTokenInfo(null);
    }
}
