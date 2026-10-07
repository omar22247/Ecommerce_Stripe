package com.omar.payment_service.client;

import com.omar.payment_service.dto.PaymentStatusUpdateRequest;
import com.omar.payment_service.dto.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ecommerce-service")
public interface OrderServiceClient {

    @PostMapping("/api/v1/internal/orders/payment-status")
    void updatePaymentStatus(@RequestBody PaymentStatusUpdateRequest request);
}