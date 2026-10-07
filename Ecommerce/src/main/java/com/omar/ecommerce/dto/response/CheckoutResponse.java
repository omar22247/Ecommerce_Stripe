package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Payment checkout session for an order")
public record CheckoutResponse(
        @Schema(description = "Order ID")
        UUID orderId,
        @Schema(description = "URL to redirect the user to for payment")
        String checkoutUrl
) {}
