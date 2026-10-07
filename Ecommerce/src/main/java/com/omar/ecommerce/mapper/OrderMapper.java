package com.omar.ecommerce.mapper;

import com.omar.ecommerce.dto.response.OrderItemResponse;
import com.omar.ecommerce.dto.response.OrderResponse;
import com.omar.ecommerce.entity.Order;
import com.omar.ecommerce.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    OrderResponse toDto(Order order);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "productId", source = "product.id")
    OrderItemResponse toItemDto(OrderItem orderItem);
}