package com.omar.ecommerce.service;

import com.omar.ecommerce.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface JwtService {
    String generateAccessToken(User user);
    String extractEmail(String token);
    boolean isTokenValid(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    UUID extractUserId(String token);
    String extractRole(String jwt);
}