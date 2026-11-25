package com.gestion.dto.auth;

import java.util.List;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        String email,
        List<String> roles
) {}
