package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Error details")
public record ErrorResponse(
        @Schema(description = "When the error occurred")
        LocalDateTime timestamp,
        @Schema(description = "HTTP status code", example = "404")
        int status,
        @Schema(description = "HTTP status reason", example = "Not Found")
        String error,
        @Schema(description = "Error message", example = "Product not found")
        String message,
        @Schema(description = "Additional error details")
        List<String> errors
) {}
