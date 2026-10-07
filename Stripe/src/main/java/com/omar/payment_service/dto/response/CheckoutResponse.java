package com.omar.payment_service.dto.response;


import java.util.UUID;

public record CheckoutResponse(
        UUID orderId,
        String checkoutUrl
) {}
