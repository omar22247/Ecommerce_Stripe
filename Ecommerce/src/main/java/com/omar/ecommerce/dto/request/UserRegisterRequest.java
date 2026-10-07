package com.omar.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "New account details")
public record UserRegisterRequest(
        @Schema(description = "First name", example = "Omar")
        @NotBlank
        String firstName,
        @Schema(description = "Last name", example = "Ali")
        @NotBlank
        String lastName,
        @Schema(description = "Email address; must be unique", example = "user@example.com")
        @NotBlank
        @Email
        String email,
        @Schema(description = "Password (at least 8 characters)", example = "password123", format = "password")
        @NotBlank
        @Size(min = 8)
        String password
) {}
