package com.omar.payment_service.exception;

import java.util.UUID;

public class PaymentAlreadyExistsException extends RuntimeException {
    public PaymentAlreadyExistsException(UUID orderId) {
        super("Payment already exists for order " + orderId);
    }
}