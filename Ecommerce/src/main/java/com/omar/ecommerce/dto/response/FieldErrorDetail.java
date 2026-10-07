package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A validation error on a single field")
public record FieldErrorDetail(
        @Schema(description = "Name of the invalid field", example = "email")
        String field,
        @Schema(description = "Why the value is invalid", example = "must be a well-formed email address")
        String message
) {}
