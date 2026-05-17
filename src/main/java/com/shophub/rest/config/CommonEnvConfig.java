package com.shophub.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class CommonEnvConfig {

    /*====================COOKIES SETUP=====================*/
    @Value("${env.cookie.secure}")
    private boolean IS_SECURING_COOKIES;
    public boolean IS_SECURING_COOKIES() {return IS_SECURING_COOKIES;}

    @Value("${env.cookie.same-site}")
    private String SAME_SITE_COOKIES;
    public String SAME_SITE_COOKIES() {return SAME_SITE_COOKIES;}

    @Value("${env.domain}")
    private String SVC_DOMAIN;
    public String SVC_DOMAIN() {return SVC_DOMAIN;}

    /*====================JWT VALUES=====================*/
    @Value("${spring.jackson.time-zone}")
    private String DATETIME_ZONE;
    public String DATETIME_ZONE() {return DATETIME_ZONE;}

    @Value("${spring.application.name}")
    private String SVC_NAME;
    public String SVC_NAME() {return SVC_NAME;}

    @Value("${env.token.access-time}")
    private int ACCESS_EXPIRY;
    public int ACCESS_EXPIRY() {return ACCESS_EXPIRY;}

    @Value("${env.token.refresh-time}")
    private int REFRESH_EXPIRY;
    public int REFRESH_EXPIRY() {return REFRESH_EXPIRY;}

    @Value("${env.token.private-key-src}")
    private Resource PRIVATE_KEY;
    public Resource PRIVATE_KEY() {return PRIVATE_KEY;}

    @Value("${env.token.public-key-src}")
    private Resource PUBLIC_KEY;
    public Resource PUBLIC_KEY() {return PUBLIC_KEY;}
}
