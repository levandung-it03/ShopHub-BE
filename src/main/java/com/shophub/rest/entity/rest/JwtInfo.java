package com.shophub.rest.entity.rest;

import com.shophub.rest.config.rest.ETokenType;
import com.shophub.rest.entity.enums.EAuthority;
import com.shophub.rest.entity.enums.EProvider;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtInfo {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    String jti;
    Long userId;
    String fullName;
    EAuthority role;
    EProvider provider;
    Instant issuedAt;
    Instant expiresAt;
    ETokenType type;

    String signWith;
}
