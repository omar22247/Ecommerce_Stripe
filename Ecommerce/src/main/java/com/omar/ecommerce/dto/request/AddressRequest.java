package com.omar.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Shipping address details")
public record AddressRequest(
        @Schema(description = "Street and house number", example = "12 Tahrir St")
        @NotNull
        String street,
        @Schema(description = "City", example = "Cairo")
        @NotNull
        String city,
        @Schema(description = "State, province or governorate", example = "Cairo Governorate")
        @NotNull
        String state,
        @Schema(description = "Postal / ZIP code", example = "11511")
        @NotNull
        String postalCode,
        @Schema(description = "Country", example = "Egypt")
        @NotNull
        String country,
        @Schema(description = "Make this the user's default address", example = "true")
        boolean isDefault
) {
}
