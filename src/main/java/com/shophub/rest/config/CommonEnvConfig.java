package com.shophub.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class CommonEnvConfig {

    @Value("${env.biz.ordering-retried-times}")
    private int ORDERING_RETRIED_TIMES;
    public int ORDERING_RETRIED_TIMES() {return ORDERING_RETRIED_TIMES;}

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

    /*====================CLOUDINARY VALUES=====================*/

    @Value("${env.cloudinary.api-key}")
    private String CLD_NARY_API_KEY;
    public String CLD_NARY_API_KEY() {return CLD_NARY_API_KEY;}

    @Value("${env.cloudinary.api-secret}")
    private String CLD_NARY_API_SECRET;
    public String CLD_NARY_API_SECRET() {return CLD_NARY_API_SECRET;}

    @Value("${env.cloudinary.cloud-name}")
    private String CLD_NARY_CLOUD_NAME;
    public String CLD_NARY_CLOUD_NAME() {return CLD_NARY_CLOUD_NAME;}

    /*====================ASYNC THREAD POOL VALUES (overriding Common)=====================*/
    @Value("${env.async-thread-pool.prefix}")
    private String ASYNC_THREAD_POOL_PREFIX;
    public String ASYNC_THREAD_POOL_PREFIX() {return ASYNC_THREAD_POOL_PREFIX;}

    @Value("${env.async-thread-pool.min-size}")
    private int ASYNC_THREAD_POOL_MIN_SIZE;
    public int ASYNC_THREAD_POOL_MIN_SIZE() {return ASYNC_THREAD_POOL_MIN_SIZE;}

    @Value("${env.async-thread-pool.max-size}")
    private int ASYNC_THREAD_POOL_MAX_SIZE;
    public int ASYNC_THREAD_POOL_MAX_SIZE() {return ASYNC_THREAD_POOL_MAX_SIZE;}

    @Value("${env.async-thread-pool.queue-capacity}")
    private int ASYNC_THREAD_POOL_QUEUE_CAPACITY;
    public int ASYNC_THREAD_POOL_QUEUE_CAPACITY() {return ASYNC_THREAD_POOL_QUEUE_CAPACITY;}

    /*====================EMAIL SETUP=====================*/
    @Value("${spring.mail.username}")
    private String EMAIL_SENDER;
    public String EMAIL_SENDER() {return EMAIL_SENDER;}

}
