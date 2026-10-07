package com.omar.ecommerce.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "A shipping address")
public record AddressResponse(

        @Schema(description = "Address ID")
        UUID id,
        @Schema(description = "Street and house number", example = "12 Tahrir St")
        String street,
        @Schema(description = "City", example = "Cairo")
        String city,
        @Schema(description = "State, province or governorate", example = "Cairo Governorate")
        String state,
        @Schema(description = "Postal / ZIP code", example = "11511")
        String postalCode,
        @Schema(description = "Country", example = "Egypt")
        String country,
        @Schema(description = "Whether this is the user's default address")
        boolean isDefault

) {
}
