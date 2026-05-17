package com.shophub.rest.mapper;

import com.shophub.rest.dto.request.ProductUpsertedReq;
import com.shophub.rest.dto.response.ProductRes;
import com.shophub.rest.entity.Category;
import com.shophub.rest.entity.Product;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ProductMapper {

    public Product toEntity(ProductUpsertedReq request, Category category) {
        return Product.builder()
            .name(request.getName())
            .price(request.getPrice())
            .category(category)
            .description(request.getDescription())
            .stockQuantity(request.getCurrentStock())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }


    public void update(Product product, ProductUpsertedReq request, Category category) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getCurrentStock());
        product.setCategory(category);
        product.setUpdatedAt(Instant.now());
    }

    public ProductRes toResponse(Product product) {
        return ProductRes.builder()
            .name(product.getName())
            .price(product.getPrice())
            .category(product.getCategory())
            .description(product.getDescription())
            .stockQuantity(product.getStockQuantity())
            .imageUrl(product.getImageUrl())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .build();
    }
}
