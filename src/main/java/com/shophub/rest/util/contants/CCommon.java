package com.shophub.rest.util.contants;

public class CCommon {

    public static class Symbols {
        public static final String ASTERISK = "*";
        public static final String EMPTY = "";
    }

    public static class API {
        public static final int DEFAULT_STATUS = 999;
        public static final String ALL_PATH = "/*";
        public static final String POST = "POST";
        public static final String OPTIONS = "OPTIONS";
        public static final String GET = "GET";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";

        public static final String SECURE = "/api/private";
        public static final String PUBLIC = "/api/public";
        public static final String REFRESH_TOKEN = SECURE + "/auth/refresh-token";
    }

    public static class ROLE {
        public static final String ADMIN = "admin";
        public static final String USER = "user";
        public static final String AUTH = "auth";
    }

    public static class TOKEN {
        public static final String BEARER = "Bearer";
        public static final String BEARER_ = "Bearer ";
        public static final String PEM_PUBLIC = "PUBLIC";
        public static final String PEM_PRIVATE = "PRIVATE";

        public static String PEM_STARTER(String type) { return "-----BEGIN " + type + " KEY-----"; }
        public static String PEM_ENDER(String type) { return "-----END " + type + " KEY-----"; }
    }
}
