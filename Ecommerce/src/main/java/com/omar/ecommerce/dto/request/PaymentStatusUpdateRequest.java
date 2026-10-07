package com.omar.ecommerce.dto.request;

import com.omar.ecommerce.entity.OrderStatus;
import com.omar.ecommerce.entity.PaymentResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentStatusUpdateRequest(
        @NotNull UUID orderId,
        @NotNull PaymentResult status
) {}