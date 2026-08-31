package com.library.service.impl;

import com.library.dto.request.LoginRequest;
import com.library.dto.request.RegisterRequest;
import com.library.dto.response.AuthResponse;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.exception.DuplicateResourceException;
import com.library.repository.UserRepository;
import com.library.security.JwtService;
import com.library.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        String username = request.getUsername().trim();

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException(
                    "Username '" + username + "' already exists"
            );
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .mustChangePassword(false)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow();

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}