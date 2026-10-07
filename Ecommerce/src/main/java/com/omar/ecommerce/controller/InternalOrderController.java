package com.omar.ecommerce.controller;

import com.omar.ecommerce.dto.request.PaymentStatusUpdateRequest;
import com.omar.ecommerce.dto.response.ApiResponse;
import com.omar.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/orders")
@RequiredArgsConstructor
public class InternalOrderController {

    private final OrderService orderService;

    @PostMapping("/payment-status")
    public ResponseEntity<ApiResponse<Void>> updatePaymentStatus(
            @Valid @RequestBody PaymentStatusUpdateRequest request) {
        orderService.applyPaymentResult(request);
        return ResponseEntity.ok(ApiResponse.success("Payment status applied", null));
    }
}