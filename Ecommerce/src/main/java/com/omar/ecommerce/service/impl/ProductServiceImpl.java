package com.omar.ecommerce.service.impl;

import com.omar.ecommerce.dto.request.ProductRequest;
import com.omar.ecommerce.dto.response.ProductResponse;
import com.omar.ecommerce.entity.Category;
import com.omar.ecommerce.entity.Product;
import com.omar.ecommerce.exception.ResourceNotFoundException;
import com.omar.ecommerce.mapper.ProductMapper;
import com.omar.ecommerce.repository.CategoryRepository;
import com.omar.ecommerce.repository.ProductRepository;
import com.omar.ecommerce.service.ProductService;
import com.omar.ecommerce.spec.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    @Override
    public Page<ProductResponse> getProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toDto);
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Product product = productMapper.toEntity(productRequest);
        System.out.println(product.toString());
        product.setCategory(category);
        productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest productRequest) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Category category = categoryRepository.findById(productRequest.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        product.setCategory(category);
        productMapper.updateEntityFromRequest(productRequest, product);
        return productMapper.toDto(productRepository.save(product));
    }

        @Transactional
        @Override
        public void deleteProduct(UUID id) {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
            product.setActive(false);
            productRepository.save(product);

    }

    @Override
    public Page<ProductResponse> searchProducts(UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = Specification
                .where(ProductSpecifications.isActive())
                .and(ProductSpecifications.hasCategory(categoryId))
                .and(ProductSpecifications.priceGreaterThanOrEqual(minPrice))
                .and(ProductSpecifications.priceLessThanOrEqual(maxPrice));
        return productRepository.findAll(spec, pageable).map(productMapper::toDto);
    }
}
