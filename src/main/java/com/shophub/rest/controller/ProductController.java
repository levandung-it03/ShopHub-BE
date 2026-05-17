package com.shophub.rest.controller;

import com.shophub.rest.dto.request.PaginationReq;
import com.shophub.rest.dto.request.ProductImgUpsertedReq;
import com.shophub.rest.dto.request.ProductUpsertedReq;
import com.shophub.rest.dto.request.ProductSearchedReq;
import com.shophub.rest.dto.response.IdRes;
import com.shophub.rest.dto.response.PaginationRes;
import com.shophub.rest.config.rest.SuccessCodes;
import com.shophub.rest.dto.response.ProductRes;
import com.shophub.rest.entity.rest.RestApiResponse;
import com.shophub.rest.service.ProductService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.shophub.rest.util.contants.CCommon.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping(API.PUBLIC + "/product/search")
    public RestApiResponse<PaginationRes<ProductRes>> searchProducts(
        @RequestBody @Valid PaginationReq<ProductSearchedReq> request) {
        return RestApiResponse.fromSuccess(SuccessCodes.GET, productService.searchProducts(request));
    }

    @GetMapping(API.PUBLIC + "/product/{id}")
    public RestApiResponse<ProductRes> get(@PathVariable Long id) {
        return RestApiResponse.fromSuccess(SuccessCodes.GET, productService.getById(id));
    }

    @PostMapping(API.SECURE + ROLE.ADMIN + "/product")
    public RestApiResponse<IdRes> create(@RequestBody @Valid ProductUpsertedReq request) {
        return RestApiResponse.fromSuccess(SuccessCodes.CREATE, productService.create(request));
    }

    @PutMapping(API.SECURE + ROLE.ADMIN + "/product/{id}")
    public RestApiResponse<IdRes> update(
        @PathVariable Long id, @RequestBody @Valid ProductUpsertedReq request) {
        return RestApiResponse.fromSuccess(SuccessCodes.UPDATE, productService.update(id, request));
    }

    @PostMapping(API.SECURE + ROLE.ADMIN + "/product/upsert-image")
    public RestApiResponse<Map<String, String>> upsertImage(@RequestBody @Valid ProductImgUpsertedReq request) {
        return RestApiResponse.fromSuccess(SuccessCodes.CREATE, productService.upsertImage(request));
    }

    @DeleteMapping(API.SECURE + ROLE.ADMIN + "/product/{id}")
    public RestApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return RestApiResponse.fromSuccess(SuccessCodes.DELETE);
    }
}
