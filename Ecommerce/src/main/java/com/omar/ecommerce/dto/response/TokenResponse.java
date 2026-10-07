package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A new pair of tokens")
public record TokenResponse(
        @Schema(description = "JWT access token; send it as 'Authorization: Bearer <token>'")
        String accessToken,
        @Schema(description = "New refresh token")
        String refreshToken,
        @Schema(description = "Token type", example = "Bearer")
        String tokenType,
        @Schema(description = "Access token lifetime in milliseconds", example = "90000000")
        long expiresIn
) {}
