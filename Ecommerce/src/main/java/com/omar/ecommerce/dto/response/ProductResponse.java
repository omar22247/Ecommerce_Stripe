package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "A product")
public record ProductResponse(
        @Schema(description = "Product ID")
        UUID id,
        @Schema(description = "Product name", example = "Wireless Mouse")
        String name,
        @Schema(description = "Product description", example = "Ergonomic 2.4 GHz wireless mouse")
        String description,
        @Schema(description = "Unit price", example = "29.99")
        BigDecimal price,
        @Schema(description = "Units in stock", example = "100")
        Integer stockQuantity,
        @Schema(description = "False if the product has been deactivated")
        boolean active,
        @Schema(description = "Category the product belongs to")
        CategoryResponse category
) {}
