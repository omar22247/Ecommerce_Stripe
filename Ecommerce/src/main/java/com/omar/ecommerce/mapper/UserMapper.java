package com.omar.ecommerce.mapper;

import com.omar.ecommerce.dto.request.RegisterRequest;
import com.omar.ecommerce.dto.request.UserRegisterRequest;
import com.omar.ecommerce.dto.response.UserResponseDto;
import com.omar.ecommerce.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    User toEntity(UserRegisterRequest request);
}
