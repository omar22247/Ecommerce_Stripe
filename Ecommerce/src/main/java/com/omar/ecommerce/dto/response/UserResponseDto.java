package com.omar.ecommerce.dto.response;

import com.omar.ecommerce.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "A user profile")
public record UserResponseDto(
        @Schema(description = "User ID")
        UUID id,
        @Schema(description = "First name", example = "Omar")
        String firstName,
        @Schema(description = "Last name", example = "Ali")
        String lastName,
        @Schema(description = "Email address", example = "user@example.com")
        String email,
        @Schema(description = "User role", example = "CUSTOMER")
        Role role,
        @Schema(description = "Whether the account is enabled")
        boolean enabled
) {}
