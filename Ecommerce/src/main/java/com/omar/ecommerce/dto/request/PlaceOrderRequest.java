package com.omar.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Order placement details; the items come from the user's cart")
public record PlaceOrderRequest(
        @Schema(description = "ID of one of the user's addresses to ship to", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull
        UUID addressId
) {}
