package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "The user's shopping cart")
public record CartResponse(
        @Schema(description = "Cart ID")
        UUID id,
        @Schema(description = "Items in the cart")
        List<CartItemResponse> items,
        @Schema(description = "Sum of all item subtotals", example = "59.98")
        BigDecimal totalAmount
) {}
