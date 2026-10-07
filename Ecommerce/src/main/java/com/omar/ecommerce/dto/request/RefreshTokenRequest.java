package com.omar.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh token exchange request")
public record RefreshTokenRequest(
        @Schema(description = "Refresh token returned by login or a previous refresh")
        @NotBlank
        String refreshToken
) {}
