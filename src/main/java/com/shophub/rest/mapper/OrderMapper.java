package com.shophub.rest.mapper;

import com.shophub.rest.dto.request.OrderCreatedReq;
import com.shophub.rest.entity.Order;
import com.shophub.rest.entity.auth.UserProfile;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderMapper {

    public Order toEntity(OrderCreatedReq request, UserProfile user) {
        return Order.builder()
            .userCreated(user)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }
}
