package com.shophub.rest.dto.response;

import com.shophub.rest.entity.enums.EOrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderSearchedRes {
    Long id;
    EOrderStatus status;
    String shippingAddress;
    Instant updatedAt;
    Instant createdAt;
}
