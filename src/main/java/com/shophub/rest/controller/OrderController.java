package com.shophub.rest.controller;

import com.shophub.rest.config.rest.SuccessCodes;
import com.shophub.rest.dto.request.OrderCreatedReq;
import com.shophub.rest.dto.request.OrderSearchedReq;
import com.shophub.rest.dto.request.PaginationReq;
import com.shophub.rest.dto.response.IdRes;
import com.shophub.rest.dto.response.OrderSearchedRes;
import com.shophub.rest.dto.response.PaginationRes;
import com.shophub.rest.entity.Order;
import com.shophub.rest.entity.rest.RestApiResponse;
import com.shophub.rest.service.IOrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import static com.shophub.rest.util.contants.CCommon.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    IOrderService orderService;

    @PostMapping(API.SECURE + ROLE.USER + "/order/submit")
    public RestApiResponse<IdRes> submitOrder(@RequestBody @Valid OrderCreatedReq request) {
        return RestApiResponse.fromSuccess(SuccessCodes.CREATE, orderService.submitOrder(request));
    }

    @PostMapping(API.SECURE + ROLE.ADMIN + "/order/search-simple-orders")
    public RestApiResponse<PaginationRes<OrderSearchedRes>> searchSimpleOrders(
        @RequestBody @Valid PaginationReq<OrderSearchedReq> request) {
        return RestApiResponse.fromSuccess(SuccessCodes.GET, orderService.searchSimpleOrders(request));
    }

    @GetMapping(API.SECURE + ROLE.ADMIN + "/order/{id}")
    public RestApiResponse<Order> get(@PathVariable Long id) {
        return RestApiResponse.fromSuccess(SuccessCodes.GET, orderService.getById(id));
    }

    @PostMapping(value = {
        API.SECURE + ROLE.ADMIN + "/order/cancel/{id}",
        API.SECURE + ROLE.USER + "/order/cancel/{id}"
    })
    public RestApiResponse<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return RestApiResponse.fromSuccess(SuccessCodes.UPDATE);
    }

    @PostMapping(API.SECURE + ROLE.ADMIN + "/order/prepare/{id}")
    public RestApiResponse<Void> prepareOrder(@PathVariable Long id) {
        orderService.prepareOrder(id);
        return RestApiResponse.fromSuccess(SuccessCodes.UPDATE);
    }

    @PostMapping(API.SECURE + ROLE.ADMIN + "/order/delivery/{id}")
    public RestApiResponse<Void> checkInDeliveryOrder(@PathVariable Long id) {
        orderService.checkInDeliveryOrder(id);
        return RestApiResponse.fromSuccess(SuccessCodes.UPDATE);
    }

    @PostMapping(API.SECURE + ROLE.ADMIN + "/order/close/{id}")
    public RestApiResponse<Void> closeOrder(@PathVariable Long id) {
        orderService.closeOrder(id);
        return RestApiResponse.fromSuccess(SuccessCodes.UPDATE);
    }

}
