package com.shophub.rest.entity.dtrcache;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash(value = "refresh_token", timeToLive = 2_592_000)
public class RefreshToken {
    @Id
    String tokenId;
}
