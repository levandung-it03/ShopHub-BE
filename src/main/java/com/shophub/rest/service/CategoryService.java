package com.shophub.rest.service;

import com.shophub.rest.config.exception.RestServiceException;
import com.shophub.rest.config.rest.ErrorCodes;
import com.shophub.rest.dto.response.CategoryRes;
import com.shophub.rest.dto.request.CategoryUpsertedReq;
import com.shophub.rest.entity.Category;
import com.shophub.rest.mapper.CategoryMapper;
import com.shophub.rest.repository.CategoryRepository;
import com.shophub.rest.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService implements ICategoryService {
    CategoryRepository categoryRepository;
    ProductRepository productRepository;
    CategoryMapper categoryMapper;

    @Override
    public CategoryRes create(CategoryUpsertedReq request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RestServiceException(ErrorCodes.INVALID_CREDENTIALS, "Category already exists");
        }

        Category category = categoryMapper.toEntity(request);

        return categoryMapper.toResponse(
            categoryRepository.save(category)
        );
    }

    @Override
    public CategoryRes update(Long id, CategoryUpsertedReq request) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RestServiceException(ErrorCodes.INVALID_ID, "Category id not found"));

        categoryMapper.update(category, request);

        return categoryMapper.toResponse(
            categoryRepository.save(category)
        );
    }

    @Override
    public CategoryRes getById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() ->
                new RestServiceException(ErrorCodes.INVALID_ID, "Category id not found")
            );

        return categoryMapper.toResponse(category);
    }

    @Override
    public List<CategoryRes> getAll() {
        return categoryRepository.findAll()
            .stream()
            .map(categoryMapper::toResponse)
            .toList();
    }

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id))
            throw new RestServiceException(ErrorCodes.INVALID_ID);

        if (productRepository.existsByCategoryId(id))
            throw new RestServiceException(ErrorCodes.REMOVING_STRICT_DATA);

        categoryRepository.deleteById(id);
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }
}