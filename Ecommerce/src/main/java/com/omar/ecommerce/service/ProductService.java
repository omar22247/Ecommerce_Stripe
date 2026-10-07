package com.omar.ecommerce.service;

import com.omar.ecommerce.dto.request.ProductRequest;
import com.omar.ecommerce.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductService {
    Page<ProductResponse> getProducts(Pageable pageable);
    ProductResponse getProductById(UUID id);
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse updateProduct(UUID id, ProductRequest productRequest);
    void deleteProduct(UUID id);
    Page<ProductResponse> searchProducts(UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
