package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "A product category")
public record CategoryResponse(
        @Schema(description = "Category ID")
        UUID id,
        @Schema(description = "Category name", example = "Electronics")
        String name,
        @Schema(description = "Category description", example = "Phones, laptops and accessories")
        String description
) {
}
