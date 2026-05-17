package com.shophub.rest.service;

import com.shophub.rest.dto.request.OrderCreatedReq;
import com.shophub.rest.dto.request.OrderSearchedReq;
import com.shophub.rest.dto.request.PaginationReq;
import com.shophub.rest.dto.response.IdRes;
import com.shophub.rest.dto.response.OrderSearchedRes;
import com.shophub.rest.dto.response.PaginationRes;
import com.shophub.rest.entity.Order;

public interface IOrderService {

    IdRes submitOrder(OrderCreatedReq request);

    PaginationRes<OrderSearchedRes> searchSimpleOrders(PaginationReq<OrderSearchedReq> request);

    Order getById(Long id);
}
