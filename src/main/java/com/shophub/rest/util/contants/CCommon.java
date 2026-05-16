package com.shophub.rest.util.contants;

import com.shophub.rest.entity.enums.EAuthority;

public interface CCommon {

    interface Symbols {
        String ASTERISK = "*";
    }

    interface API {
        int DEFAULT_STATUS = 999;
        String ALL_PATH = "/*";
        String POST = "POST";
        String OPTIONS = "OPTIONS";
        String GET = "GET";
        String DELETE = "DELETE";
        String PUT = "PUT";

        String SECURE = "/api/private";
        String PUBLIC = "/api/public";
        String REFRESH_TOKEN = SECURE + "/auth/";
    }

    interface ROLE {
        String ADMIN = EAuthority.ADMIN.getName();
        String USER = EAuthority.USER.getName();
        String AUTH = EAuthority.AUTH.getName();
    }
}
