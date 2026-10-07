package com.omar.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Product details")
public record ProductRequest(
        @Schema(description = "Product name", example = "Wireless Mouse")
        @NotBlank
        String name,

        @Schema(description = "Optional product description", example = "Ergonomic 2.4 GHz wireless mouse")
        String description,

        @Schema(description = "Unit price, positive with at most 2 decimal places", example = "29.99")
        @NotNull
        @Positive
        @Digits(integer = 17, fraction = 2)
        BigDecimal price,

        @Schema(description = "Units in stock", example = "100", minimum = "0")
        @NotNull
        @Min(0)
        Integer stockQuantity,

        @Schema(description = "ID of the category the product belongs to", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull
        UUID categoryId
) {}
