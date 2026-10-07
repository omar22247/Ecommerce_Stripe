package com.omar.ecommerce.security;

import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String email,
        String role
) {}