package com.omar.ecommerce.service;

import com.omar.ecommerce.dto.request.CategoryRequest;
import com.omar.ecommerce.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {
    Page<CategoryResponse> getAllCategories(Pageable pageable);
    CategoryResponse getCategoryById(UUID id);
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    void  deleteCategory(UUID id);
    CategoryResponse updateCategory(UUID id, CategoryRequest categoryRequest);
}
