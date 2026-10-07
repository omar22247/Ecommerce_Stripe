package com.omar.payment_service.client;

import com.omar.payment_service.dto.PaymentStatusUpdateRequest;
import com.omar.payment_service.entity.PaymentResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusNotifier {

    private static final String ORDER_SERVICE = "orderService";

    private final OrderServiceClient orderServiceClient;

    @Retry(name = ORDER_SERVICE, fallbackMethod = "notifyPaymentResultFallback")
    @CircuitBreaker(name = ORDER_SERVICE)
    public void notifyPaymentResult(UUID orderId, PaymentResult result) {
        log.info("Notifying order service: order {} -> {}", orderId, result);
        orderServiceClient.updatePaymentStatus(new PaymentStatusUpdateRequest(orderId, result));
    }

    public void notifyPaymentResultFallback(UUID orderId, PaymentResult result, Exception e) {
        log.error("Failed to notify order service: order {} -> {}", orderId, result);
    }
}
