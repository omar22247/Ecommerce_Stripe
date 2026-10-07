package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Standard envelope wrapping every API response")
public record ApiResponse<T>(
        @Schema(description = "Whether the request succeeded", example = "true")
        boolean success,
        @Schema(description = "Human-readable result message", example = "Success")
        String message,
        @Schema(description = "Response payload; null on errors")
        T data,
        @Schema(description = "Error details; empty on success")
        List<String> errors
) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, List.of());
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, List.of());
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, List.of());
    }
}
