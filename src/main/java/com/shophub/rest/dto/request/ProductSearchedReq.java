package com.shophub.rest.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSearchedReq {
    String name;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    int minQty;
    int maxQty;
}
