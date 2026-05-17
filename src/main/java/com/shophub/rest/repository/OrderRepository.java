package com.shophub.rest.repository;

import com.shophub.rest.dto.request.OrderSearchedReq;
import com.shophub.rest.dto.response.OrderSearchedRes;
import com.shophub.rest.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT
            new com.shophub.rest.dto.response.OrderSearchedRes(
                o.id,
                o.status,
                o.shippingAddress,
                o.updatedAt,
                o.createdAt
            )
        FROM Order o
        WHERE (:#{#req.fullNameCreatedBy} IS NULL OR o.userCreated.fullName LIKE CONCAT('%', :#{#req.fullNameCreatedBy},'%'))
    """)
    Page<OrderSearchedRes> searchSimpleOrders(@Param("req") OrderSearchedReq req, Pageable pageable);
}
