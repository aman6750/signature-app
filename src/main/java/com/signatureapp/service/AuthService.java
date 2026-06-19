package com.signatureapp.service;

import com.signatureapp.dto.AuthResponse;
import com.signatureapp.dto.LoginRequest;
import com.signatureapp.dto.RegisterRequest;
import com.signatureapp.exception.BadRequestException;
import com.signatureapp.exception.UnauthorizedException;
import com.signatureapp.model.User;
import com.signatureapp.repository.UserRepository;
import com.signatureapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditService auditService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        User savedUser = userRepository.save(user);

        auditService.log(savedUser, null, "USER_REGISTER",
                "New user registered with email: " + savedUser.getEmail());

        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    auditService.log(null, null, "LOGIN_FAILED",
                            "Login attempt with non-existent email: " + request.getEmail());
                    return new UnauthorizedException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            auditService.log(user, null, "LOGIN_FAILED",
                    "Wrong password for user: " + user.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }

        auditService.log(user, null, "USER_LOGIN",
                "Successful login by: " + user.getEmail());

        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}