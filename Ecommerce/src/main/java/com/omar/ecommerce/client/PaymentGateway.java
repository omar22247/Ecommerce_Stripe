package com.omar.ecommerce.client;

import com.omar.ecommerce.client.dto.CreateCheckoutSessionRequest;
import com.omar.ecommerce.exception.OrderStateConflictException;
import com.omar.ecommerce.exception.PaymentServiceUnavailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentGateway {

    private static final String PAYMENT_SERVICE = "paymentService";

    private final PaymentServiceClient paymentServiceClient;

    @Retry(name = PAYMENT_SERVICE, fallbackMethod = "createCheckoutSessionFallback")
    @CircuitBreaker(name = PAYMENT_SERVICE)
    public String createCheckoutSession(UUID orderId, BigDecimal amount, String currency) {
        log.info("Requesting checkout session for order {}", orderId);
        return paymentServiceClient.createCheckoutSession(
                new CreateCheckoutSessionRequest(orderId, amount, currency));
    }

    public String createCheckoutSessionFallback(UUID orderId, BigDecimal amount, String currency, Exception e) {
        if (e instanceof FeignException fe && fe.status() == HttpStatus.CONFLICT.value()) {
            throw new OrderStateConflictException("A checkout session already exists for order " + orderId);
        }
        if (e instanceof FeignException fe && fe.status() >= 400 && fe.status() < 500) {
            throw fe;
        }
        log.error("Payment service unavailable for order {}: {}", orderId, e.getMessage());
        throw new PaymentServiceUnavailableException("Payment service is currently unavailable, please try again later");
    }
}
