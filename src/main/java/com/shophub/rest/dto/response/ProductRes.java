package com.shophub.rest.dto.response;

import com.shophub.rest.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRes {
    Long id;
    Category category;
    String name;
    String description;
    BigDecimal price;
    Integer stockQuantity;
    String imageUrl;
    Instant createdAt;
    Instant updatedAt;
}
