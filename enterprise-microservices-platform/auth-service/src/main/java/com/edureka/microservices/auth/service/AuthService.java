package com.edureka.microservices.auth.service;

import com.edureka.microservices.auth.dto.AuthResponse;
import com.edureka.microservices.auth.dto.LoginRequest;
import com.edureka.microservices.auth.dto.RegisterRequest;
import com.edureka.microservices.auth.entity.Role;
import com.edureka.microservices.auth.entity.User;
import com.edureka.microservices.auth.repository.UserRepository;
import com.edureka.microservices.auth.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            logger.warn("Registration failed: username {} already exists", request.username());
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User(
            request.username(),
            passwordEncoder.encode(request.password()),
            Role.USER
        );

        userRepository.save(user);
        logger.info("User registered successfully: {}", request.username());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> {
                logger.warn("Login failed: user {} not found", request.username());
                return new IllegalArgumentException("Invalid credentials");
            });

        if (!user.getActive()) {
            logger.warn("Login failed: user {} is inactive", request.username());
            throw new IllegalArgumentException("User account is inactive");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            logger.warn("Login failed: invalid password for user {}", request.username());
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateToken(user);
        logger.info("User logged in successfully: {}", request.username());

        return new AuthResponse(
            token,
            user.getUsername(),
            user.getRole().name(),
            86400000L
        );
    }

}
