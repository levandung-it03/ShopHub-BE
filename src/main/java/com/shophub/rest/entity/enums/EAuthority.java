package com.shophub.rest.entity.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum EAuthority {
    AUTH("auth"), // Authorized all
    SYS_ADMIN("sysadmin"), // SYS_ADMIN
    ADMIN("admin"), // ADMIN
    MMANAGER("mmanager"), // MARKET_MANAGER
    SUPPORT("support"), // SUPPORT
    USER("user"), // USER
    ;
    String name;
    EAuthority(String name) {
        this.name = name;
    }
}
