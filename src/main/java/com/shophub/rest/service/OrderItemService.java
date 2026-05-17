package com.shophub.rest.service;

import com.shophub.rest.entity.OrderItem;
import com.shophub.rest.repository.OrderItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderItemService {
    OrderItemRepository orderItemRepository;

    public boolean existsByProductId(Long productId) {
        return orderItemRepository.existsByProductId(productId);
    }
}
