package com.shophub.rest.config.rest;

import com.shophub.rest.entity.rest.CookiesHolder;
import com.shophub.rest.entity.rest.JwtInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
@Getter
public class RequestCtxDataDelivery {
    @Setter
    private JwtInfo jwtInfo;
    private CookiesHolder cookiesHolder;

    public RequestCtxDataDelivery() {
        this.cookiesHolder = new CookiesHolder();
    }
}
