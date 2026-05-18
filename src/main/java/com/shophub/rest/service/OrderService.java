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
import com.shophub.rest.entity.Invoice;
import com.shophub.rest.entity.Order;
import com.shophub.rest.entity.OrderItem;
import com.shophub.rest.entity.OrderStatusHistory;
import com.shophub.rest.entity.auth.UserProfile;
import com.shophub.rest.entity.enums.EAuthority;
import com.shophub.rest.entity.enums.EOrderStatus;
import com.shophub.rest.mapper.InvoiceMapper;
import com.shophub.rest.mapper.OrderMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    InvoiceRepository invoiceRepository;
    OrderMapper orderMapper;
    RequestCtxDataDelivery reqCtxData;
    EmailService emailService;
    CommonEnvConfig env;
    InvoiceMapper invoiceMapper;

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
        orderStatusHistRepo.save(OrderStatusHistory.builder()
            .order(order)
            .status(EOrderStatus.ORDERED)
            .changedAt(Instant.now())
            .changedBy(userProfile)
            .build());

        String userEmail = userProfile.getAccount().getEmail();
        emailService.sendSimpleEmail(userEmail,
            CEmailText.OrderMsg.CREATION_TITLE(createdOrder.getId()),
            CEmailText.OrderMsg.USER_CREATION_HTML(createdOrder));

        UserProfile uniqueAdminProfile = userProfileRepository.getUniqueAdminProfile()
            .orElseThrow(() -> new RestServiceException(ErrorCodes.ADMIN_NOT_FOUND));
        String adminEmail = uniqueAdminProfile.getAccount().getEmail();
        emailService.sendSimpleEmail(adminEmail,
            CEmailText.OrderMsg.CREATION_TITLE(order.getId()),
            CEmailText.OrderMsg.ADMIN_CREATION_HTML(order));

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

    /**
     * Biz:
     * </br> 1. Order can be canceled by User.
     * </br> 2. Order can be canceled by User.
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void cancelOrder(Long id) {
        UserProfile currentUser = userProfileRepository
            .findById(reqCtxData.getAuthzedTokenInfo().getUserId())
            .orElseThrow(() -> new RestServiceException(ErrorCodes.USER_NOTFOUND));

        Order order = orderRepository.findById(id).orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID));

        var isOrderOwnerCanceling = currentUser.getAccount().getEmail().equals(order.getUserCreated().getAccount().getEmail());
        if (isOrderOwnerCanceling) {
            var updatedOrder = this.cancelOrder(order, currentUser);
            var uniqueAdminProfile = userProfileRepository.getUniqueAdminProfile()
                .orElseThrow(() -> new RestServiceException(ErrorCodes.ADMIN_NOT_FOUND));
            emailService.sendSimpleEmail(uniqueAdminProfile.getAccount().getEmail(),
                CEmailText.OrderMsg.CANCEL_TITLE(order.getId()),
                CEmailText.OrderMsg.NOTICE_CANCELING_BY_OWNER_HTML(updatedOrder));
        }

        var isAdminCanceling = currentUser.getAccount().getAuthority().getAuthority().equals(EAuthority.ADMIN);
        if (isAdminCanceling) {
            var updatedOrder = this.cancelOrder(order, currentUser);
            emailService.sendSimpleEmail(order.getUserCreated().getAccount().getEmail(),
                CEmailText.OrderMsg.CANCEL_TITLE(updatedOrder.getId()),
                CEmailText.OrderMsg.NOTICE_CANCELING_BY_ADMIN_HTML(updatedOrder, currentUser));
        }
    }

    private Order cancelOrder(Order order, UserProfile currentUser) {
        if (order.getStatus().equals(EOrderStatus.ORDERED))
            throw new RestServiceException(ErrorCodes.ORDER_CANCELING_NOT_ORDERED_STS);

        order.setStatus(EOrderStatus.CANCELED);
        order.setUpdatedAt(Instant.now());

        Order updatedOrder = orderRepository.save(order);
        orderStatusHistRepo.save(OrderStatusHistory.builder()
            .order(updatedOrder)
            .status(EOrderStatus.CANCELED)
            .changedAt(Instant.now())
            .changedBy(currentUser)
            .build());

        return updatedOrder;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void prepareOrder(Long id) {
        UserProfile adminProfile = userProfileRepository
            .findById(reqCtxData.getAuthzedTokenInfo().getUserId())
            .orElseThrow(() -> new RestServiceException(ErrorCodes.USER_NOTFOUND));

        Order order = orderRepository.findById(id).orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID));

        if (order.getStatus().equals(EOrderStatus.ORDERED))
            throw new RestServiceException(ErrorCodes.ORDER_CANCELING_NOT_ORDERED_STS);

        order.setStatus(EOrderStatus.PREPARING);
        order.setUpdatedAt(Instant.now());

        Order updatedOrder = orderRepository.save(order);
        orderStatusHistRepo.save(OrderStatusHistory.builder()
            .order(updatedOrder)
            .status(EOrderStatus.PREPARING)
            .changedAt(Instant.now())
            .changedBy(adminProfile)
            .build());

        emailService.sendSimpleEmail(order.getUserCreated().getAccount().getEmail(),
            CEmailText.OrderMsg.PREPARE_TITLE(order.getId()),
            CEmailText.OrderMsg.PREPARE_HTML(order, adminProfile.getAccount().getEmail()));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void checkInDeliveryOrder(Long id) {
        UserProfile adminProfile = userProfileRepository
            .findById(reqCtxData.getAuthzedTokenInfo().getUserId())
            .orElseThrow(() -> new RestServiceException(ErrorCodes.USER_NOTFOUND));

        Order order = orderRepository.findById(id).orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID));

        if (order.getStatus().equals(EOrderStatus.PREPARING))
            throw new RestServiceException(ErrorCodes.ORDER_DELIVERY_NOT_PREPARING_STS);

        order.setStatus(EOrderStatus.IN_DELIVERY);
        order.setUpdatedAt(Instant.now());

        Order updatedOrder = orderRepository.save(order);
        orderStatusHistRepo.save(OrderStatusHistory.builder()
            .order(updatedOrder)
            .status(EOrderStatus.IN_DELIVERY)
            .changedAt(Instant.now())
            .changedBy(adminProfile)
            .build());

        emailService.sendSimpleEmail(order.getUserCreated().getAccount().getEmail(),
            CEmailText.OrderMsg.DELIVERY_TITLE(order.getId()),
            CEmailText.OrderMsg.DELIVERY_HTML(order, adminProfile.getAccount().getEmail()));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void closeOrder(Long id) {
        UserProfile adminProfile = userProfileRepository
            .findById(reqCtxData.getAuthzedTokenInfo().getUserId())
            .orElseThrow(() -> new RestServiceException(ErrorCodes.USER_NOTFOUND));

        Order order = orderRepository.findById(id).orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID));

        if (order.getStatus().equals(EOrderStatus.IN_DELIVERY))
            throw new RestServiceException(ErrorCodes.ORDER_CLOSE_NOT_DELIVERY_STS);

        order.setStatus(EOrderStatus.CLOSED);
        order.setUpdatedAt(Instant.now());

        Order updatedOrder = orderRepository.save(order);
        orderStatusHistRepo.save(OrderStatusHistory.builder()
            .order(updatedOrder)
            .status(EOrderStatus.CLOSED)
            .changedAt(Instant.now())
            .changedBy(adminProfile)
            .build());

        Invoice invoice = invoiceMapper.createInvoice(order);
        Invoice createdInvoice = invoiceRepository.save(invoice);

        emailService.sendSimpleEmail(order.getUserCreated().getAccount().getEmail(),
            CEmailText.OrderMsg.BILL_FOR_USER_TITLE(order.getId()),
            CEmailText.OrderMsg.BILL_FOR_USER_HTML(createdInvoice));

        emailService.sendSimpleEmail(order.getUserCreated().getAccount().getEmail(),
            CEmailText.OrderMsg.BILL_FOR_ADMIN_TITLE(order.getId()),
            CEmailText.OrderMsg.BILL_FOR_ADMIN_HTML(createdInvoice));
    }

}
