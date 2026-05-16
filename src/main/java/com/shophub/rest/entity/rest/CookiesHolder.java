package com.shophub.rest.entity.rest;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CookiesHolder {
    String jwtAccess;
    String jwtRefresh;
}
