package com.shophub.rest.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpsertedReq {
    @NotEmpty
    @Length(max = 256)
    String name;

    @NotEmpty
    @Length(max = 256)
    String description;

    @NotNull
    Long categoryId;

    @NotNull
    BigDecimal price;

    @NotNull
    @Min(0)
    Integer currentStock;
}
