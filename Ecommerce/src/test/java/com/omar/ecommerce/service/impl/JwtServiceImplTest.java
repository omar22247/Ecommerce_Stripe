package com.omar.ecommerce.service.impl;

import com.omar.ecommerce.entity.User;
import com.omar.ecommerce.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private User user;
    private UUID userId;

    private static final String TEST_SECRET = "0123456789_this_is_a_test_secret_key_32+";

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();

        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 3_600_000L);

        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setEmail("omar@example.com");
        user.setRole(Role.ADMIN);
    }

    @Test
    void extractRole_returnsRoleStoredInToken() {

        String token = jwtService.generateAccessToken(user);
        String role = jwtService.extractRole(token);
        assertEquals("ADMIN", role);
    }
    @Test
    void extractEmail_returnsSubject() {
        String token = jwtService.generateAccessToken(user);
        assertEquals("omar@example.com", jwtService.extractEmail(token));
    }

    @Test
    void extractUserId_returnsId() {
        String token = jwtService.generateAccessToken(user);
        assertEquals(userId, jwtService.extractUserId(token));
    }

    @Test
    void isTokenValid_returnsTrueForFreshToken() {
        String token = jwtService.generateAccessToken(user);
        assertTrue(jwtService.isTokenValid(token));
    }


    @Test
    void isTokenValid_returnsFalseForTamperedToken() {
        String token = jwtService.generateAccessToken(user);
        String tampered = token.substring(0, token.length() - 1)
                + (token.endsWith("a") ? "b" : "a");
        assertFalse(jwtService.isTokenValid(tampered));
    }
}