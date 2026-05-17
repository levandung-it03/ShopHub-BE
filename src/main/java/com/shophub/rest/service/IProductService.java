package com.shophub.rest.service;

import com.shophub.rest.dto.request.PaginationReq;
import com.shophub.rest.dto.request.ProductImgUpsertedReq;
import com.shophub.rest.dto.request.ProductSearchedReq;
import com.shophub.rest.dto.request.ProductUpsertedReq;
import com.shophub.rest.dto.response.IdRes;
import com.shophub.rest.dto.response.PaginationRes;
import com.shophub.rest.dto.response.ProductRes;
import com.shophub.rest.entity.Product;

import java.util.Map;

public interface IProductService {

    PaginationRes<ProductRes> searchProducts(PaginationReq<ProductSearchedReq> request);

    IdRes create(ProductUpsertedReq request);

    IdRes update(Long id, ProductUpsertedReq request);

    Map<String, String> upsertImage(ProductImgUpsertedReq request);

    void delete(Long id);

    ProductRes getById(Long id);
}
