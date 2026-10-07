package com.omar.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Product to add to the cart")
public record AddToCartRequest(
        @Schema(description = "ID of the product to add", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull
        UUID productId,

        @Schema(description = "Quantity to add; added to any quantity already in the cart", example = "2", minimum = "1")
        @NotNull
        @Min(1)
        Integer quantity
) {}
