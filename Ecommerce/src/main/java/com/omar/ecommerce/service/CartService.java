package com.omar.ecommerce.service;

import com.omar.ecommerce.dto.request.AddToCartRequest;
import com.omar.ecommerce.dto.request.UpdateCartItemRequest;
import com.omar.ecommerce.dto.response.CartResponse;
import com.omar.ecommerce.entity.CartItem;

import java.util.UUID;

public interface CartService {
    CartResponse getMyCart(UUID userId);
    CartResponse addItemToCart(UUID userId, AddToCartRequest request);
    CartResponse removeItemFromCart(UUID userId, Long cartItemId);
    CartResponse updateCartItemQuantity(UUID userId, Long cartItemId, UpdateCartItemRequest request);
}