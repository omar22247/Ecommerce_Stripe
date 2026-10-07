package com.omar.ecommerce.service.impl;

import com.omar.ecommerce.dto.request.AddToCartRequest;
import com.omar.ecommerce.dto.request.UpdateCartItemRequest;
import com.omar.ecommerce.dto.response.CartItemResponse;
import com.omar.ecommerce.dto.response.CartResponse;
import com.omar.ecommerce.entity.Cart;
import com.omar.ecommerce.entity.CartItem;
import com.omar.ecommerce.entity.Product;
import com.omar.ecommerce.entity.User;
import com.omar.ecommerce.exception.InsufficientStockException;
import com.omar.ecommerce.exception.ResourceNotFoundException;
import com.omar.ecommerce.mapper.CartMapper;
import com.omar.ecommerce.repository.CartRepository;
import com.omar.ecommerce.repository.ProductRepository;
import com.omar.ecommerce.repository.UserRepository;
import com.omar.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Transactional
    @Override
    public CartResponse getMyCart(UUID userId) {
        Cart cart = cartRepository.findByUserIdWithItemsAndProducts(userId)
                .orElseGet(() -> createCartForUser(userId));

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(UUID userId, AddToCartRequest request) {

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.productId()));
        Cart userCart = cartRepository.findByUserIdWithItemsAndProducts(userId)
                .orElseGet(() -> createCartForUser(userId));
        CartItem cartItem = userCart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.productId()))
                .findFirst()
                .orElse(null);
        int existingQuantity = cartItem == null ? 0 : cartItem.getQuantity();

        if (existingQuantity + request.quantity() > product.getStockQuantity()) {
            throw new InsufficientStockException(
                    "Not enough stock for product: " + product.getName()
            );
        }
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.quantity());
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(request.quantity());
            cartItem.setCart(userCart);
            userCart.getItems().add(cartItem);
        }
        cartRepository.save(userCart);
        return buildCartResponse(userCart);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(UUID userId, Long cartItemId) {
        Cart userCart = cartRepository.findByUserIdWithItemsAndProducts(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user with id: " + userId));
        CartItem cartItem = userCart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
        userCart.getItems().remove(cartItem);
        cartRepository.save(userCart);

        return buildCartResponse(userCart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(UUID userId, Long cartItemId, UpdateCartItemRequest request) {
        Cart userCart = cartRepository.findByUserIdWithItemsAndProducts(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user with id: " + userId));

        CartItem cartItem = userCart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

            Product product=cartItem.getProduct();

            if(product.getStockQuantity()<request.quantity()) {
                throw new InsufficientStockException(
                        "Not enough stock for product: " + product.getName()
                );
            }
            
        cartItem.setQuantity(request.quantity());
            cartRepository.save(userCart);

        return buildCartResponse(userCart);
    }

    private Cart createCartForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart newCart = new Cart();
        newCart.setUser(user);
        return cartRepository.save(newCart);
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponseWithSubtotal)
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getId(), items, totalAmount);
    }

    private CartItemResponse toItemResponseWithSubtotal(CartItem item) {
        CartItemResponse dto = cartMapper.toItemDto(item);
        BigDecimal subtotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(dto.id(), dto.productId(), dto.productName(), dto.unitPrice(), dto.quantity(), subtotal);
    }
}
