package com.omar.ecommerce.controller;

import com.omar.ecommerce.dto.request.PlaceOrderRequest;
import com.omar.ecommerce.dto.response.ApiResponse;
import com.omar.ecommerce.dto.response.CheckoutResponse;
import com.omar.ecommerce.dto.response.OrderResponse;
import com.omar.ecommerce.security.AuthenticatedUser;
import com.omar.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Place, view and cancel orders of the authenticated user")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Place an order",
            description = "Creates an order from the current contents of the authenticated user's cart, "
                    + "ships it to the given address, reserves stock and empties the cart.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order placed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed or cart is empty"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Not enough stock for an item")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse order = orderService.placeOrder(user.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", order));
    }

    @Operation(summary = "List my orders", description = "Returns all orders of the authenticated user.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orders retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> listMyOrders(
            @AuthenticationPrincipal AuthenticatedUser user) {
        List<OrderResponse> orders = orderService.listOrders(user.userId());
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
    }

    @Operation(summary = "Get an order", description = "Returns one of the authenticated user's orders, including its items.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Order belongs to another user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "Order ID") @PathVariable UUID orderId) {
        OrderResponse order = orderService.getOrderById(user.userId(), orderId);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
    }

    @Operation(summary = "Cancel an order",
            description = "Cancels an order and restores product stock. Only orders in PENDING, CONFIRMED "
                    + "or PROCESSING status can be cancelled.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order cancelled"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Order cannot be cancelled from its current status"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Order belongs to another user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "Order ID") @PathVariable UUID orderId) {
        OrderResponse order = orderService.cancelOrder(user.userId(), orderId);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
    }

    @Operation(summary = "Start payment for an order",
            description = "Creates a payment checkout session for a PENDING order and returns the URL "
                    + "to redirect the user to.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Checkout session created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Order belongs to another user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Order is not PENDING or checkout already started"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Payment service unavailable")
    })
    @PostMapping("/{orderId}/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "Order ID") @PathVariable UUID orderId) {
        CheckoutResponse checkout = orderService.createCheckout(user.userId(), orderId);
        return ResponseEntity.ok(ApiResponse.success("Checkout session created", checkout));
    }
}
