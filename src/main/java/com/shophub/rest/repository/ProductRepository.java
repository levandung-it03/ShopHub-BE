package com.shophub.rest.repository;

import com.shophub.rest.dto.request.ProductSearchedReq;
import com.shophub.rest.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT P FROM Product P
        WHERE   (:#{#prod.name} IS NULL OR P.name LIKE CONCAT('%', :#{#prod.name},'%'))
            AND (:#{#prod.minPrice} IS NULL OR P.price >= :#{#prod.minPrice})
            AND (:#{#prod.maxPrice} IS NULL OR P.price <= :#{#prod.maxPrice})
            AND (:#{#prod.minQty} IS NULL OR P.stockQuantity >= :#{#prod.minQty})
            AND (:#{#prod.maxQty} IS NULL OR P.stockQuantity <= :#{#prod.maxQty})
    """)
    Page<Product> searchProducts(@Param("prod") ProductSearchedReq product, Pageable pageable);

    @Transactional
    @Modifying
    @Query("""
        UPDATE Product p
        SET p.imageUrl = :#{#prod.imageUrl},
            p.imagePublicId = :#{#prod.imagePublicId}
        WHERE p.id = :#{#prod.id}
    """)
    void updateImageUrlByProductId(@Param("prod") Product product);

    boolean existsByCategoryId(Long id);
}
