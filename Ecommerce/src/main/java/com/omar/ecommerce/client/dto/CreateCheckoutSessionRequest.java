package com.omar.ecommerce.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCheckoutSessionRequest(
        UUID orderId,
        BigDecimal amount,
        String currency
) {}
