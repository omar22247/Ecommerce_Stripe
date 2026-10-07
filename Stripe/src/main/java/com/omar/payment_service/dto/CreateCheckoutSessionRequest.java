package com.omar.payment_service.dto;

import java.math.BigDecimal;
import java.util.UUID;
public record CreateCheckoutSessionRequest(
        UUID orderId,
        BigDecimal amount,
        String currency
) {}