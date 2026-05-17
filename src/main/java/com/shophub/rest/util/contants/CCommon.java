package com.shophub.rest.util.contants;

import com.shophub.rest.entity.enums.EAuthority;

public interface CCommon {

    interface Symbols {
        String ASTERISK = "*";
        String EMPTY = "";
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

    interface TOKEN {
        String BEARER = "Bearer";
        String BEARER_ = "Bearer ";
        String PEM_PUBLIC = "PUBLIC";
        String PEM_PRIVATE = "PRIVATE";

        static String PEM_STARTER(String type) { return "-----BEGIN " + type + " KEY-----"; }
        static String PEM_ENDER(String type) { return "-----END " + type + " KEY-----"; }
    }
}
