package com.crio.videorentalsystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.crio.videorentalsystem.dto.AuthResponse;
import com.crio.videorentalsystem.dto.LoginRequest;
import com.crio.videorentalsystem.dto.RegisterRequest;
import com.crio.videorentalsystem.entity.Role;
import com.crio.videorentalsystem.entity.User;
import com.crio.videorentalsystem.exception.UserAlreadyExistsException;
import com.crio.videorentalsystem.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_WhenRoleIsNull_DefaultsToCustomerAndHashesPassword() {
        RegisterRequest request = new RegisterRequest("john@test.com", "rawPass", "John", "Doe", null);
        User savedUser = User.builder()
            .id(1L)
            .email("john@test.com")
            .password("encodedPass")
            .firstName("John")
            .lastName("Doe")
            .role(Role.CUSTOMER)
            .build();

        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(Role.CUSTOMER, response.role());
        assertEquals("john@test.com", response.email());
        verify(passwordEncoder).encode("rawPass");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WhenEmailAlreadyExists_ThrowsUserAlreadyExistsException() {
        RegisterRequest request = new RegisterRequest("john@test.com", "rawPass", "John", "Doe", Role.CUSTOMER);
        when(userRepository.existsByEmail("john@test.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ReturnsAuthResponse() {
        LoginRequest request = new LoginRequest("admin@test.com", "adminPass");
        User user = User.builder()
            .id(2L)
            .email("admin@test.com")
            .password("encodedPass")
            .firstName("Jane")
            .lastName("Admin")
            .role(Role.ADMIN)
            .build();

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("Login successful", response.message());
        assertEquals(Role.ADMIN, response.role());
    }
}