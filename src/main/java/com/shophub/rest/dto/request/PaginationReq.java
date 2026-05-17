package com.shophub.rest.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationReq <T> {
    public static final int SIZE = 30;

    @Min(1)
    int page;

    T filteredBy;

    @Max(1)
    @Min(-1)
    int sortedDirection;

    @NotEmpty
    String sortedOn;

    public Pageable getPageable(Class<?> clazz) {
        Sort.Direction direction;
        if (sortedDirection == -1) {
            direction = Sort.Direction.DESC;
        } else {
            direction = Sort.Direction.ASC;
        }

        var existsSortedField = Arrays.stream(clazz.getDeclaredFields())
            .anyMatch(f -> f.toString().equals(sortedOn));
        if (existsSortedField) {
            return PageRequest.of(page - 1, SIZE, Sort.by(direction, sortedOn));
        } else {
            return PageRequest.of(page - 1, SIZE);
        }
    }
}
