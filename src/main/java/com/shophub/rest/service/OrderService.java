package com.shophub.rest.service;

import com.shophub.rest.config.CommonEnvConfig;
import com.shophub.rest.config.exception.RestServiceException;
import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.config.rest.RequestCtxDataDelivery;
import com.shophub.rest.dto.request.OrderCreatedReq;
import com.shophub.rest.dto.request.OrderSearchedReq;
import com.shophub.rest.dto.request.PaginationReq;
import com.shophub.rest.dto.response.IdRes;
import com.shophub.rest.dto.response.OrderSearchedRes;
import com.shophub.rest.dto.response.PaginationRes;
import com.shophub.rest.entity.Order;
import com.shophub.rest.entity.OrderItem;
import com.shophub.rest.entity.OrderStatusHistory;
import com.shophub.rest.entity.auth.UserProfile;
import com.shophub.rest.mapper.OrderMapper;
import com.shophub.rest.mapper.OrderStatusHistoryMapper;
import com.shophub.rest.repository.*;
import com.shophub.rest.service.tools.EmailService;
import com.shophub.rest.service.trans.OrderTransService;
import com.shophub.rest.util.contants.CEmailText;
import jakarta.persistence.OptimisticLockException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.shophub.rest.dto.request.OrderCreatedReq.OrderItemCreatedReq;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements IOrderService {
    OrderRepository orderRepository;
    OrderTransService orderTransService;
    UserProfileRepository userProfileRepository;
    OrderStatusHistoryRepository orderStatusHistRepo;
    OrderMapper orderMapper;
    OrderStatusHistoryMapper orderStatusHistoryMapper;
    RequestCtxDataDelivery reqCtxData;
    EmailService emailService;
    CommonEnvConfig env;

    /**
     * Biz:
     * </br> 1. Create root Order.
     * </br> 2. Create mapped OrderItems, update Products stock-qty (OptimisticLock implemented).
     * </br> 3. Create OrderStatusHistory (ORDERED) by User.
     * </br> 4. Send email to both admin and user.
     */
    @Override
    public IdRes submitOrder(OrderCreatedReq request) {
        // Using Map for fast looking-up qty (from request)
        Map<Long, OrderItemCreatedReq> orderItemsMap = request.getOrderItems().stream()
            .collect(Collectors.toMap(OrderCreatedReq.OrderItemCreatedReq::getProductId, item -> item));

        if (orderItemsMap.size() != request.getOrderItems().size())
            throw new RestServiceException(ErrorCodes.INVALID_IDS);

        UserProfile userProfile = userProfileRepository
            .findById(reqCtxData.getAuthzedTokenInfo().getUserId())
            .orElseThrow(() -> new RestServiceException(ErrorCodes.USER_NOTFOUND));
        Order order = orderMapper.toEntity(request, userProfile);

        List<OrderItem> orderItems = this.buildOrderItemsAndUpdateProductsWithRetrying(orderItemsMap, order);
            order.setOrderItems(orderItems);

        Order createdOrder = orderRepository.save(order);
        OrderStatusHistory createdOrderStatus = orderStatusHistRepo
            .save(orderStatusHistoryMapper.toEntity(order, userProfile));

        String userEmail = userProfile.getAccount().getEmail();
        emailService.sendSimpleEmail(userEmail,
            CEmailText.Order.CREATION_TITLE(userEmail),
            CEmailText.Order.CREATION_HTML());

        UserProfile uniqueAdminProfile = userProfileRepository.getUniqueAdminProfile()
            .orElseThrow(() -> new RestServiceException(ErrorCodes.ADMIN_NOT_FOUND));
        String adminEmail = uniqueAdminProfile.getAccount().getEmail();
        emailService.sendSimpleEmail(userEmail,
            CEmailText.Order.CREATION_TITLE(adminEmail),
            CEmailText.Order.CREATION_HTML());

        return IdRes.builder().id(createdOrder.getId()).build();
    }

    private List<OrderItem> buildOrderItemsAndUpdateProductsWithRetrying(Map<Long, OrderItemCreatedReq> itemsMap, Order order) {
        for (int times = 1; times <= env.ORDERING_RETRIED_TIMES(); times++) {
            try {
                return orderTransService.buildOrderItemsAndUpdateProducts(itemsMap, order);
            } catch (OptimisticLockException e) {
                continue;   // Retry
            }
        }
        throw new RestServiceException(ErrorCodes.BUSY_ORDERING_SVC);
    }

    @Override
    public PaginationRes<OrderSearchedRes> searchSimpleOrders(PaginationReq<OrderSearchedReq> request) {
        Pageable pageable = request.getPageable(Order.class);
        Page<OrderSearchedRes> simpleOrders = orderRepository.searchSimpleOrders(request.getFilteredBy(), pageable);
        return PaginationRes.<OrderSearchedRes>builder()
            .page(request.getPage())
            .size(PaginationReq.SIZE)
            .data(simpleOrders.getContent())
            .build();
    }

    @Override
    public Order getById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID));
    }

    public void cancelOrder() {
        // made by User
        // made by Admin
    }

    public void prepareOrder() {
        // confirm by Admin (just Order.ORDERED is valid)
    }

    public void checkInDeliveryOrder() {
        // confirm by Admin (delivery-service third-party)
    }

    public void closeOrder() {
        // confirm by Admin (when Order is confirmed by successful delivery)
    }

}
