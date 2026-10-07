package com.omar.ecommerce.service.impl;

import com.omar.ecommerce.dto.request.LoginRequest;
import com.omar.ecommerce.dto.request.UserRegisterRequest;
import com.omar.ecommerce.dto.response.LoginResponse;
import com.omar.ecommerce.dto.response.TokenResponse;
import com.omar.ecommerce.dto.response.UserResponseDto;
import com.omar.ecommerce.entity.RefreshToken;
import com.omar.ecommerce.entity.Role;
import com.omar.ecommerce.entity.User;
import com.omar.ecommerce.exception.AccountDisabledException;
import com.omar.ecommerce.exception.DuplicateResourceException;
import com.omar.ecommerce.exception.InvalidCredentialsException;
import com.omar.ecommerce.mapper.UserMapper;
import com.omar.ecommerce.repository.RefreshTokenRepository;
import com.omar.ecommerce.repository.UserRepository;
import com.omar.ecommerce.security.AppUserDetails;
import com.omar.ecommerce.service.AuthService;
import com.omar.ecommerce.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${refresh-token-expiration}")
    private long refreshTokenExpiration;


    @Transactional
    @Override
    public UserResponseDto register(UserRegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateResourceException("Email is already in use");
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
    @Transactional
    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            Authentication auth=new UsernamePasswordAuthenticationToken(request.email(), request.password());
            authentication = authenticationManager.authenticate(auth
            );
        } catch (DisabledException e) {
            throw new AccountDisabledException("Account is disabled");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = generateRefreshToken(user).getToken();

        return new LoginResponse(userMapper.toDto(user), accessToken,refreshToken, "Bearer", accessTokenExpiration);
    }
    @Transactional
    @Override
    public TokenResponse refreshToken(String refreshTokenValue) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        if (stored.isRevoked()) {
            throw new InvalidCredentialsException("Refresh token has been revoked");
        }
        if (stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidCredentialsException("Refresh token has expired");
        }

        User user = stored.getUser();

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        String newAccessToken = jwtService.generateAccessToken(user);
        RefreshToken newRefreshToken = generateRefreshToken(user);

        return new TokenResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                "Bearer",
                accessTokenExpiration
        );
    }

    private RefreshToken generateRefreshToken(User user) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }
}
