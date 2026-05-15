package com.shophub.rest.entity.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum EAuthority {
    SA("sa"), // SYS_ADMIN
    AD("ad"), // ADMIN
    MM("mm"), // MARKET_MANAGER
    SP("sp"), // SUPPORT
    US("us"), // USER
    ;
    String name;
    EAuthority(String name) {
        this.name = name;
    }
}
