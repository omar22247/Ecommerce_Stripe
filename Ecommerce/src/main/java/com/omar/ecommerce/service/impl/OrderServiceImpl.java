package com.omar.ecommerce.service.impl;

import com.omar.ecommerce.client.PaymentGateway;
import com.omar.ecommerce.dto.request.PaymentStatusUpdateRequest;
import com.omar.ecommerce.dto.request.PlaceOrderRequest;
import com.omar.ecommerce.dto.response.CheckoutResponse;
import com.omar.ecommerce.dto.response.OrderResponse;
import com.omar.ecommerce.entity.*;
import com.omar.ecommerce.exception.InsufficientStockException;
import com.omar.ecommerce.exception.InvalidOrderException;
import com.omar.ecommerce.exception.OrderStateConflictException;
import com.omar.ecommerce.exception.ResourceNotFoundException;
import com.omar.ecommerce.mapper.OrderMapper;
import com.omar.ecommerce.repository.AddressRepository;
import com.omar.ecommerce.repository.CartRepository;
import com.omar.ecommerce.repository.OrderRepository;
import com.omar.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentGateway paymentGateway;

    @Value("${payment.currency:usd}")
    private String currency;

    @Override
    @Transactional
    public OrderResponse placeOrder(UUID userId, PlaceOrderRequest request) {

        Cart cart = cartRepository.findByUserIdWithItemsAndProducts(userId)
                .orElseThrow(() ->
                        new InvalidOrderException("Cart not found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new InvalidOrderException("Your cart is empty.");
        }

        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found with id: " + request.addressId()));

        if (!address.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Address does not belong to the user.");
        }

        Order order = buildOrder(cart, address);
        cart.getItems().clear();

        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderResponse> listOrders(UUID userId) {
        List<Order> orders = orderRepository.findByUserIdWithItems(userId);

        return orders.stream()
                  .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(UUID userId,UUID orderId) {
        Order order=orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId));
        if (!order.getUser().getId().equals(userId)) {
          throw new AccessDeniedException("Order does not belong to the user.");
        }
        return orderMapper.toDto(order) ;
    }

    @Transactional
    @Override
    public OrderResponse cancelOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findByIdWithItemsAndProducts(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Order does not belong to the user.");
        }

        if (!order.getStatus().canTransitionTo(OrderStatus.CANCELLED)) {
            throw new InvalidOrderException(
                    "Order cannot be cancelled from its current status: " + order.getStatus());
        }

        restoreStock(order);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public CheckoutResponse createCheckout(UUID userId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Order does not belong to the user.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderStateConflictException(
                    "Order is " + order.getStatus() + " and cannot be paid");
        }

        String checkoutUrl = paymentGateway.createCheckoutSession(orderId, order.getTotalAmount(), currency);
        return new CheckoutResponse(orderId, checkoutUrl);
    }

    private void restoreStock(Order order) {

        for (OrderItem orderItem : order.getItems()) {
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
        }
    }

    private Order buildOrder(Cart cart, Address address) {

        Order order = new Order();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            validateProduct(product, cartItem.getQuantity());

            product.setStockQuantity(
                    product.getStockQuantity() - cartItem.getQuantity());

            OrderItem orderItem = createOrderItem(order, cartItem);

            order.getItems().add(orderItem);

            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);

        order.setShippingStreet(address.getStreet());
        order.setShippingCity(address.getCity());
        order.setShippingPostalCode(address.getPostalCode());
        order.setShippingCountry(address.getCountry());

        return order;
    }

    private void validateProduct(Product product, int quantity) {

        if (!product.isActive()) {
            throw new InvalidOrderException(
                    "Product '" + product.getName() + "' is inactive.");
        }

        if (quantity > product.getStockQuantity()) {
            throw new InsufficientStockException(
                    "Not enough stock for product: " + product.getName());
        }
    }

    private OrderItem createOrderItem(Order order, CartItem cartItem) {

        Product product = cartItem.getProduct();

        BigDecimal subtotal =
                product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setProductName(product.getName());
        item.setUnitPrice(product.getPrice());
        item.setQuantity(cartItem.getQuantity());
        item.setSubtotal(subtotal);

        return item;
    }

    @Transactional
    @Override
    public void applyPaymentResult(PaymentStatusUpdateRequest request) {
        Order order = orderRepository.findByIdWithItemsAndProducts(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + request.orderId()));

        OrderStatus current = order.getStatus();

        switch (request.status()) {
            case SUCCESS -> {
                if (current == OrderStatus.PAID) {
                    return;
                }
                if (!current.canTransitionTo(OrderStatus.PAID)) {
                    throw new OrderStateConflictException(
                            "Order is " + current + " and cannot be marked as paid");
                }
                order.setStatus(OrderStatus.PAID);
            }
            case FAILED -> {
                if (current == OrderStatus.FAILED || current == OrderStatus.CANCELLED) {
                    return;
                }
                if (!current.canTransitionTo(OrderStatus.FAILED)) {
                    throw new OrderStateConflictException(
                            "Order is " + current + " and cannot be marked as failed");
                }
                restoreStock(order);
                order.setStatus(OrderStatus.FAILED);
            }
        }

        orderRepository.save(order);
    }

}