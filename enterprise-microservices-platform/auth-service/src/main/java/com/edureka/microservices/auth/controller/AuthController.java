package com.edureka.microservices.auth.controller;

import com.edureka.microservices.auth.dto.AuthResponse;
import com.edureka.microservices.auth.dto.LoginRequest;
import com.edureka.microservices.auth.dto.RegisterRequest;
import com.edureka.microservices.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Register request for username: {}", request.username());
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login request for username: {}", request.username());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
