package com.omar.ecommerce.repository;

import com.omar.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = "category")
    @Override
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "category")
    @Override
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}