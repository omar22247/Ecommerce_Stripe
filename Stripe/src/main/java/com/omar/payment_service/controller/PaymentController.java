package com.omar.payment_service.controller;

import com.omar.payment_service.dto.CreateCheckoutSessionRequest;
import com.omar.payment_service.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> createCheckoutSession(
            @RequestBody CreateCheckoutSessionRequest request
    ) throws Exception {

        String checkoutUrl =
                paymentService.createCheckoutSession(request);

        return ResponseEntity.ok(checkoutUrl);
    }
}