package com.omar.ecommerce.service.impl;

import com.omar.ecommerce.dto.request.CategoryRequest;
import com.omar.ecommerce.dto.response.CategoryResponse;
import com.omar.ecommerce.entity.Category;
import com.omar.ecommerce.exception.DuplicateResourceException;
import com.omar.ecommerce.exception.ResourceNotFoundException;
import com.omar.ecommerce.mapper.CategoryMapper;
import com.omar.ecommerce.repository.CategoryRepository;
import com.omar.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(categoryMapper::toDto);
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return categoryMapper.toDto(category);
    }

    @Transactional
    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.findByName(categoryRequest.name()).isPresent()) {
            throw new DuplicateResourceException("Category name already in use");
        }
        Category newCategory = categoryMapper.toEntity(categoryRequest);
        categoryRepository.save(newCategory);
        return categoryMapper.toDto(newCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryRepository.delete(category);
    }

    @Transactional
    @Override
    public CategoryResponse updateCategory(UUID id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        categoryRepository.findByName(categoryRequest.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Category name already in use");
                });

        categoryMapper.updateEntityFromRequest(categoryRequest, category);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }
}
