package com.shophub.rest.service;

import com.shophub.rest.dto.request.CategoryUpsertedReq;
import com.shophub.rest.dto.response.CategoryRes;

import java.util.List;

public interface ICategoryService {
    CategoryRes create(CategoryUpsertedReq request);

    CategoryRes update(Long id, CategoryUpsertedReq request);

    CategoryRes getById(Long id);

    List<CategoryRes> getAll();

    void delete(Long id);
}
