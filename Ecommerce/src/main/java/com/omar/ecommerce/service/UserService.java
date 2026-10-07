package com.omar.ecommerce.service;

import com.omar.ecommerce.dto.response.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService {
    UserResponseDto getUserById(UUID id);
}
