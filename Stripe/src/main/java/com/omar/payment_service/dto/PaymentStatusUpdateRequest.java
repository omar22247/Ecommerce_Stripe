package com.omar.payment_service.dto;

import com.omar.payment_service.entity.PaymentResult;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentStatusUpdateRequest(
        @NotNull UUID orderId,
        @NotNull PaymentResult status
) {}