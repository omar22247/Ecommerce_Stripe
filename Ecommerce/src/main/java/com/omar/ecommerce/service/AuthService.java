package com.omar.ecommerce.service;

import com.omar.ecommerce.dto.request.LoginRequest;
import com.omar.ecommerce.dto.request.UserRegisterRequest;
import com.omar.ecommerce.dto.response.LoginResponse;
import com.omar.ecommerce.dto.response.TokenResponse;
import com.omar.ecommerce.dto.response.UserResponseDto;
import com.omar.ecommerce.entity.RefreshToken;

public interface AuthService {
    UserResponseDto register(UserRegisterRequest request);
    LoginResponse login(LoginRequest request);
    TokenResponse refreshToken(String refreshToken);
}