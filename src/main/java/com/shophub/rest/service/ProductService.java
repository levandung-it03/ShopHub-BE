package com.shophub.rest.service;

import com.shophub.rest.config.exception.RestServiceException;
import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.dto.request.PaginationReq;
import com.shophub.rest.dto.request.ProductImgUpsertedReq;
import com.shophub.rest.dto.request.ProductSearchedReq;
import com.shophub.rest.dto.request.ProductUpsertedReq;
import com.shophub.rest.dto.response.IdRes;
import com.shophub.rest.dto.response.PaginationRes;
import com.shophub.rest.dto.response.ProductRes;
import com.shophub.rest.entity.Category;
import com.shophub.rest.entity.Product;
import com.shophub.rest.mapper.ProductMapper;
import com.shophub.rest.repository.CategoryRepository;
import com.shophub.rest.repository.OrderItemRepository;
import com.shophub.rest.repository.ProductRepository;
import com.shophub.rest.service.tools.ImgCloudUpload;
import jakarta.persistence.OptimisticLockException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProductService implements IProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    OrderItemRepository orderItemRepository;
    ImgCloudUpload imgCloudUpload;
    ProductMapper productMapper;

    public ProductService(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public PaginationRes<ProductRes> searchProducts(PaginationReq<ProductSearchedReq> request) {
        Pageable pageable = request.getPageable(Product.class);
        Page<Product> productsPage = productRepository.searchProducts(request.getFilteredBy(), pageable);
        return PaginationRes.<ProductRes>builder()
            .page(request.getPage())
            .size(PaginationReq.SIZE)
            .pagesQty(productsPage.getTotalPages())
            .data(productsPage.getContent().stream().map(productMapper::toResponse).toList())
            .build();
    }

    @Override
    public IdRes create(ProductUpsertedReq request) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID));
        Product product = productMapper.toEntity(request, category);
        Product productRes = productRepository.save(product);
        return IdRes.builder().id(productRes.getId()).build();
    }

    @Override
    public IdRes update(Long id, ProductUpsertedReq request) {
        try {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID));
            Product product = productRepository.findById(id)
                .orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID));

            productMapper.update(product, request, category);
            Product productRes = productRepository.save(product);
            return IdRes.builder().id(productRes.getId()).build();
        } catch (OptimisticLockException e) {
            throw new RestServiceException(ErrorCodes.DUPLICATE_UPSERT);
        }
    }

    @Override
    public Map<String, String> upsertImage(ProductImgUpsertedReq request) {
        try {
            Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID));
            if (product.getImagePublicId() != null)
                imgCloudUpload.remove(product.getImagePublicId());

            var infoMap = imgCloudUpload.uploadAndReturnInfo("product", request.getProductImage());
            product.setImagePublicId(infoMap.get("publicId"));
            product.setImageUrl(infoMap.get("url"));
            productRepository.updateImageUrlByProductId(product);
            return Map.of("imageUrl", infoMap.get("url"));
        } catch (Exception e) {
            throw new RestServiceException(e);
        }
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id))
            throw new RestServiceException(ErrorCodes.INVALID_ID);

        if (orderItemRepository.existsByProductId(id))
            throw new RestServiceException(ErrorCodes.REMOVING_STRICT_DATA);

        productRepository.deleteById(id);
    }

    @Override
    public ProductRes getById(Long id) {
        return productMapper.toResponse(productRepository.findById(id)
            .orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID)));
    }
}
