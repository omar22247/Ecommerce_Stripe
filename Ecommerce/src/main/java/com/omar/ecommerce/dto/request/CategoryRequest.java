package com.omar.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Category details")
public record CategoryRequest(
        @Schema(description = "Unique category name", example = "Electronics")
        @NotBlank
        String name,
        @Schema(description = "Optional category description", example = "Phones, laptops and accessories")
        String description
) {
}
