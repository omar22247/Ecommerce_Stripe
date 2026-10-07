package com.omar.payment_service.controller;

import com.omar.payment_service.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class StripeWebhookController {

    private final String webhookSecret;
    private final PaymentService paymentService;

    public StripeWebhookController(
            @Value("${stripe.webhook-secret}") String webhookSecret, PaymentService paymentService) {
        this.webhookSecret = webhookSecret;
        this.paymentService = paymentService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        System.out.println("🔥 WEBHOOK REACHED");

        try {
            Event event = Webhook.constructEvent(
                    payload,
                    signature,
                    webhookSecret
            );
            System.out.println("Verified webhook: " + event.getType());

            paymentService.handleWebhook(event);

            return ResponseEntity.ok("Webhook received");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Invalid webhook");
        }
    }

}