package com.shophub.rest.mapper;

import com.shophub.rest.dto.response.CategoryRes;
import com.shophub.rest.dto.request.CategoryUpsertedReq;
import com.shophub.rest.entity.Category;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryUpsertedReq request) {
        return Category.builder()
            .name(request.getName())
            .build();
    }

    public void update(Category root, CategoryUpsertedReq newInfo) {
        root.setName(newInfo.getName());
        root.setUpdatedAt(Instant.now());
    }

    public CategoryRes toResponse(Category category) {
        return CategoryRes.builder()
            .id(category.getId())
            .name(category.getName())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }
}