package com.omar.ecommerce.spec;

import com.omar.ecommerce.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductSpecifications {
    public static Specification<Product> hasCategory(UUID categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }
    public static Specification<Product> isActive() {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("active"), true);
    }
    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal minPrice) {

        return (root, criteriaQuery, criteriaBuilder)-> {
            if(minPrice == null){
                return criteriaBuilder.conjunction();
            }
             return  criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);

        };

    }
    public static Specification<Product> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }
}
