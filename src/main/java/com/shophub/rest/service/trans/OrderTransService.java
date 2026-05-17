package com.shophub.rest.service.trans;

import com.shophub.rest.config.exception.RestServiceException;
import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.entity.Order;
import com.shophub.rest.entity.OrderItem;
import com.shophub.rest.entity.Product;
import com.shophub.rest.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.shophub.rest.dto.request.OrderCreatedReq.OrderItemCreatedReq;

@Service
public class OrderTransService {
    ProductRepository productRepository;

    @Transactional(rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRES_NEW)
    public List<OrderItem> buildOrderItemsAndUpdateProducts(
        Map<Long, OrderItemCreatedReq> orderItemsMap, Order creatingOrder
    ) throws OptimisticLockException {
        // Get with Version
        List<Product> products = productRepository.findAllById(orderItemsMap.keySet());
        if (orderItemsMap.size() != products.size())
            throw new RestServiceException(ErrorCodes.INVALID_IDS);

        List<OrderItem> orderItems = new ArrayList<>();
        for (Product product : products) {
            var orderedQty = orderItemsMap.get(product.getId()).getQty();

            // Updating Product Qty (stock)
            product.setStockQuantity(product.getStockQuantity() - orderedQty);
            if (product.getStockQuantity() < 0)
                throw new RestServiceException(ErrorCodes.NEGATIVE_QTY);

            // Mapping OrderItem
            orderItems.add(OrderItem.builder()
                .order(creatingOrder)
                .quantity(orderedQty)
                .priceAtPurchase(product.getPrice())
                .build());
        }

        productRepository.saveAll(products);    // Update with Version
        return orderItems;
    }
}
