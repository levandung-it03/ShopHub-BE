package com.shophub.rest.service.auth;

import com.shophub.rest.entity.rest.JwtInfo;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    public boolean checkIsValid(String token) {
        return true;
    }


    public boolean isAuthorizedToken(String token) {
        return false;
    }

    public JwtInfo read(String token) {
        return null;
    }
}
