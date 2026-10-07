package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Result of a successful login")
public record LoginResponse(
        @Schema(description = "The logged-in user")
        UserResponseDto user,
        @Schema(description = "JWT access token; send it as 'Authorization: Bearer <token>'")
        String accessToken,
        @Schema(description = "Refresh token used to obtain a new access token")
        String refreshToken,
        @Schema(description = "Token type", example = "Bearer")
        String tokenType,
        @Schema(description = "Access token lifetime in milliseconds", example = "90000000")
        long expiresIn
) {}
