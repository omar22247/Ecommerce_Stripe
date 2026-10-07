package com.omar.ecommerce.mapper;

import com.omar.ecommerce.dto.response.CartItemResponse;
import com.omar.ecommerce.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "subtotal", ignore = true)
    CartItemResponse toItemDto(CartItem cartItem);
}