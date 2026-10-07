package com.omar.ecommerce.controller;

import com.omar.ecommerce.dto.request.AddressRequest;
import com.omar.ecommerce.dto.response.AddressResponse;
import com.omar.ecommerce.dto.response.ApiResponse;
import com.omar.ecommerce.security.AuthenticatedUser;
import com.omar.ecommerce.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "Manage the shipping addresses of the authenticated user")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "List my addresses", description = "Returns all shipping addresses of the authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Addresses retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses(
            @AuthenticationPrincipal AuthenticatedUser user) {
        List<AddressResponse> addresses = addressService.getAllAddresses(user.userId());
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully", addresses));
    }

    @Operation(summary = "Add an address",
            description = "Adds a shipping address. If isDefault is true, it replaces the current default address.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Address added"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token")
    })
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse address = addressService.addAddresses(user.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added successfully", address));
    }

    @Operation(summary = "Update an address",
            description = "Updates one of the authenticated user's addresses. If isDefault is true, it replaces the current default address.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PutMapping("/me/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "ID of the address to update") @PathVariable UUID addressId,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse address = addressService.updateAddresses(user.userId(), addressId, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", address));
    }

    @Operation(summary = "Delete an address",
            description = "Deletes one of the authenticated user's addresses. The default address cannot be deleted; set another address as default first.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Address deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found")
    })
    @DeleteMapping("/me/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "ID of the address to delete") @PathVariable UUID addressId) {
        addressService.deleteAddresses(user.userId(), addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }
}
