package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "A line item in the cart")
public record CartItemResponse(
        @Schema(description = "Cart item ID; use it to update or remove the item", example = "1")
        Long id,
        @Schema(description = "Product ID")
        UUID productId,
        @Schema(description = "Product name", example = "Wireless Mouse")
        String productName,
        @Schema(description = "Current unit price of the product", example = "29.99")
        BigDecimal unitPrice,
        @Schema(description = "Quantity in the cart", example = "2")
        Integer quantity,
        @Schema(description = "unitPrice x quantity", example = "59.98")
        BigDecimal subtotal
) {}
