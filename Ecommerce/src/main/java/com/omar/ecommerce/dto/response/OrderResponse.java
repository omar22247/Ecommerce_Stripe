package com.omar.ecommerce.dto.response;

import com.omar.ecommerce.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "An order with its items and shipping address")
public record OrderResponse(
        @Schema(description = "Order ID")
        UUID id,
        @Schema(description = "ID of the user who placed the order")
        UUID userId,
        @Schema(description = "Current order status", example = "PENDING")
        OrderStatus status,
        @Schema(description = "Total order amount", example = "59.98")
        BigDecimal totalAmount,
        @Schema(description = "Shipping street", example = "12 Tahrir St")
        String shippingStreet,
        @Schema(description = "Shipping city", example = "Cairo")
        String shippingCity,
        @Schema(description = "Shipping state", example = "Cairo Governorate")
        String shippingState,
        @Schema(description = "Shipping postal code", example = "11511")
        String shippingPostalCode,
        @Schema(description = "Shipping country", example = "Egypt")
        String shippingCountry,
        @Schema(description = "Items in the order")
        List<OrderItemResponse> items,
        @Schema(description = "When the order was placed")
        LocalDateTime createdAt,
        @Schema(description = "When the order was last updated")
        LocalDateTime updatedAt,
        @Schema(description = "When the order was delivered; null until delivered")
        LocalDateTime deliveredAt
) {}
