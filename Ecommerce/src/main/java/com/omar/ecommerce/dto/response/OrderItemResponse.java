package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "A line item in an order")
public record OrderItemResponse(
        @Schema(description = "Order item ID", example = "1")
        Long id,
        @Schema(description = "ID of the order this item belongs to")
        UUID orderId,
        @Schema(description = "Product ID")
        UUID productId,
        @Schema(description = "Product name at the time of ordering", example = "Wireless Mouse")
        String productName,
        @Schema(description = "Unit price at the time of ordering", example = "29.99")
        BigDecimal unitPrice,
        @Schema(description = "Quantity ordered", example = "2")
        Integer quantity,
        @Schema(description = "unitPrice x quantity", example = "59.98")
        BigDecimal subtotal
) {
}
