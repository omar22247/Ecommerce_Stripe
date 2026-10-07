package com.omar.ecommerce.controller;

import com.omar.ecommerce.dto.request.AddToCartRequest;
import com.omar.ecommerce.dto.request.UpdateCartItemRequest;
import com.omar.ecommerce.dto.response.ApiResponse;
import com.omar.ecommerce.dto.response.CartResponse;
import com.omar.ecommerce.security.AuthenticatedUser;
import com.omar.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "View and modify the shopping cart of the authenticated user")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get my cart", description = "Returns the authenticated user's cart with its items and total amount.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cart retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart(@AuthenticationPrincipal AuthenticatedUser user) {
        CartResponse cart = cartService.getMyCart(user.userId());
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cart));
    }

    @Operation(summary = "Add item to cart",
            description = "Adds a product to the cart. If the product is already in the cart, the quantity is increased. "
                    + "The total quantity cannot exceed the product's stock.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item added; returns the updated cart"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Not enough stock")
    })
    @PostMapping("/me/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addItemToCart(user.userId(), request);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart successfully", cart));
    }

    @Operation(summary = "Remove item from cart", description = "Removes a cart item and returns the updated cart.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item removed; returns the updated cart"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @DeleteMapping("/me/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "ID of the cart item (not the product ID)") @PathVariable Long cartItemId) {
        CartResponse cart = cartService.removeItemFromCart(user.userId(), cartItemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully", cart));
    }

    @Operation(summary = "Update item quantity",
            description = "Sets the quantity of a cart item. The quantity cannot exceed the product's stock.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Quantity updated; returns the updated cart"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cart item not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Not enough stock")
    })
    @PutMapping("/me/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "ID of the cart item (not the product ID)") @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateCartItemQuantity(user.userId(), cartItemId, request);
        return ResponseEntity.ok(ApiResponse.success("Cart item quantity updated successfully", cart));
    }
}
