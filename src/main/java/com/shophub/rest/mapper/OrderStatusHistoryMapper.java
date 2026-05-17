package com.shophub.rest.mapper;

import com.shophub.rest.entity.Order;
import com.shophub.rest.entity.OrderStatusHistory;
import com.shophub.rest.entity.auth.UserProfile;
import com.shophub.rest.entity.enums.EOrderStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderStatusHistoryMapper {

    public OrderStatusHistory toEntity(Order order, UserProfile changedBy) {
        return OrderStatusHistory.builder()
            .order(order)
            .status(EOrderStatus.ORDERED)
            .changedAt(Instant.now())
            .changedBy(changedBy)
            .build();
    }
}
