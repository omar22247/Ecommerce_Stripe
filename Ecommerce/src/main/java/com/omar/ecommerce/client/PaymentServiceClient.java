package com.omar.ecommerce.client;

import com.omar.ecommerce.client.dto.CreateCheckoutSessionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/payments/checkout")
    String createCheckoutSession(@RequestBody CreateCheckoutSessionRequest request);
}
