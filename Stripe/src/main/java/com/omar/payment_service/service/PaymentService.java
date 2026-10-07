package com.omar.payment_service.service;

import com.omar.payment_service.client.OrderStatusNotifier;
import com.omar.payment_service.client.StripeClient;
import com.omar.payment_service.dto.CreateCheckoutSessionRequest;
import com.omar.payment_service.entity.CheckoutSessionResult;
import com.omar.payment_service.entity.Payment;
import com.omar.payment_service.entity.PaymentResult;
import com.omar.payment_service.entity.PaymentStatus;
import com.omar.payment_service.exception.PaymentAlreadyExistsException;
import com.omar.payment_service.repository.PaymentRepository;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final StripeClient stripeClient;
    private final OrderStatusNotifier orderStatusNotifier;
    private final PaymentRepository paymentRepository;


    @Transactional
    public String createCheckoutSession(CreateCheckoutSessionRequest request) throws Exception {

        Payment existing = paymentRepository
                .findByOrderId(request.orderId())
                .orElse(null);

        if (existing != null) {
            throw new PaymentAlreadyExistsException(request.orderId());
        }

        CheckoutSessionResult session =
                stripeClient.createCheckoutSession(
                        request.amount(),
                        request.currency(),
                        request.orderId().toString()
                );

        Payment payment = Payment.builder()
                .orderId(request.orderId())
                .amount(request.amount())
                .currency(request.currency())
                .status(PaymentStatus.PENDING)
                .checkoutSessionId(session.id())
                .build();

        paymentRepository.save(payment);

        return session.url();
    }
    @Transactional
    public void handleWebhook(Event event) {
        switch (event.getType()) {

            case "checkout.session.completed" -> handleCheckoutCompleted(event);

            case "checkout.session.expired"   -> handlePaymentFailed(event);

            default -> System.out.println("Unhandled event: " + event.getType());
        }
    }


    private StripeObject deserialize(Event event) {
        System.out.println(event.toString());
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        return deserializer.getObject().orElseGet(() -> {
            try {
                return deserializer.deserializeUnsafe();
            } catch (EventDataObjectDeserializationException e) {
                throw new IllegalStateException(
                        "Failed to deserialize " + event.getType() + " (api_version "
                                + event.getApiVersion() + ")", e);
            }
        });
    }

    private void handleCheckoutCompleted(Event event) {
        Session session = (Session) deserialize(event);

        Payment payment = paymentRepository.findByCheckoutSessionId(session.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "No payment for session " + session.getId()));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }
        if (!"paid".equals(session.getPaymentStatus())) {
            return;
        }

        orderStatusNotifier.notifyPaymentResult(payment.getOrderId(), PaymentResult.SUCCESS);

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentIntentId(session.getPaymentIntent());
        paymentRepository.save(payment);
    }
    private void handlePaymentFailed(Event event) {
        Session session = (Session) deserialize(event);

        Payment payment = paymentRepository.findByCheckoutSessionId(session.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "No payment for session " + session.getId()));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            return;
        }

        orderStatusNotifier.notifyPaymentResult(payment.getOrderId(), PaymentResult.FAILED);

        payment.setStatus(PaymentStatus.FAILED);
        payment.setPaymentIntentId(session.getPaymentIntent());
        paymentRepository.save(payment);
    }


}