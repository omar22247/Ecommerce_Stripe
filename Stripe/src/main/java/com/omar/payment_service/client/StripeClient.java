package com.omar.payment_service.client;

import com.omar.payment_service.entity.CheckoutSessionResult;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Component
public class StripeClient {

    public StripeClient(
            @Value("${stripe.secret-key}") String secretKey
    ) {
        Stripe.apiKey = secretKey;
    }

    public CheckoutSessionResult createCheckoutSession(
            BigDecimal amount,
            String currency,
            String orderId
    ) throws Exception {

        long amountInCents = amount
                .multiply(BigDecimal.valueOf(100))
                .longValueExact();
        long expiresAt = Instant.now().plus(Duration.ofMinutes(30)).getEpochSecond();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(
                                SessionCreateParams.Mode.PAYMENT
                        )
                        .setExpiresAt(expiresAt)
                        .addExcludedPaymentMethodType(SessionCreateParams.ExcludedPaymentMethodType.SEPA_DEBIT)
                        .addExcludedPaymentMethodType(SessionCreateParams.ExcludedPaymentMethodType.US_BANK_ACCOUNT)
                        .setSuccessUrl(
                                "http://localhost:8081/api/v1/orders/"+orderId
                        )
                        .setCancelUrl(
                                "http://localhost:8081/api/v1/orders/"+orderId
                        )
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("usd")
                                                        .setUnitAmount(amountInCents)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Order " + orderId)
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .putMetadata("orderId", orderId)
                        .build();

        Session session = Session.create(params);
      return new CheckoutSessionResult(session.getId(), session.getUrl());
    }

    public String retrieveSessionUrl(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        return "open".equals(session.getStatus()) ? session.getUrl() : null;
    }

}