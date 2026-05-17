package com.shophub.rest.controller;

import com.shophub.rest.config.rest.SuccessCodes;
import com.shophub.rest.dto.request.CategoryUpsertedReq;
import com.shophub.rest.dto.response.CategoryRes;
import com.shophub.rest.entity.rest.RestApiResponse;
import com.shophub.rest.service.ICategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.shophub.rest.util.contants.CCommon.*;

@RestController
@RequestMapping(API.SECURE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    ICategoryService categoryService;

    @PostMapping
    public RestApiResponse<CategoryRes> create(@Valid @RequestBody CategoryUpsertedReq request) {
        return RestApiResponse.fromSuccess(SuccessCodes.CREATE, categoryService.create(request));
    }

    @PutMapping(ROLE.ADMIN + "/{id}")
    public RestApiResponse<CategoryRes> update(@PathVariable Long id, @Valid @RequestBody CategoryUpsertedReq request) {
        return RestApiResponse.fromSuccess(SuccessCodes.UPDATE, categoryService.update(id, request));
    }

    @GetMapping(value = {ROLE.USER + "/category/{id}", ROLE.ADMIN + "/category/{id}"})
    public RestApiResponse<CategoryRes> getById(@PathVariable Long id) {
        return RestApiResponse.fromSuccess(SuccessCodes.GET, categoryService.getById(id));
    }

    @GetMapping
    public RestApiResponse<List<CategoryRes>> getAll() {
        return RestApiResponse.fromSuccess(SuccessCodes.GET, categoryService.getAll());
    }

    @DeleteMapping("/{id}")
    public RestApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return RestApiResponse.fromSuccess(SuccessCodes.DELETE);
    }
}
