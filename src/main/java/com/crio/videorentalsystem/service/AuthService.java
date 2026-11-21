package com.crio.videorentalsystem.service;

import com.crio.videorentalsystem.dto.AuthResponse;
import com.crio.videorentalsystem.dto.LoginRequest;
import com.crio.videorentalsystem.dto.RegisterRequest;
import com.crio.videorentalsystem.entity.Role;
import com.crio.videorentalsystem.entity.User;
import com.crio.videorentalsystem.exception.ResourceNotFoundException;
import com.crio.videorentalsystem.exception.UserAlreadyExistsException;
import com.crio.videorentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("User already exists with email: " + request.email());
        }

        Role assignedRole = (request.role() != null) ? request.role() : Role.CUSTOMER;

        User user = User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .role(assignedRole)
            .build();

        User savedUser = userRepository.save(user);

        return new AuthResponse(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            savedUser.getRole(),
            "User registered successfully"
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.email()));

        return new AuthResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole(),
            "Login successful"
        );
    }
}