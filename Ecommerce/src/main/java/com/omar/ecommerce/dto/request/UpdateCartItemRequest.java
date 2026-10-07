package com.omar.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "New quantity for a cart item")
public record UpdateCartItemRequest(
        @Schema(description = "New quantity (replaces the current one)", example = "3", minimum = "1")
        @NotNull
        @Min(1)
        Integer quantity) {
}
