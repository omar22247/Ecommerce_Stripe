package com.omar.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Login credentials")
public record LoginRequest(
        @Schema(description = "Account email", example = "user@example.com")
        @NotBlank
        @Email
        String email,
        @Schema(description = "Account password (at least 8 characters)", example = "password123", format = "password")
        @NotBlank
        @Size(min = 8)
        String password) {
}
