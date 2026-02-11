package com.edureka.microservices.auth.dto;

public record AuthResponse(
    String token,
    String username,
    String role,
    long expiresIn
) {}
