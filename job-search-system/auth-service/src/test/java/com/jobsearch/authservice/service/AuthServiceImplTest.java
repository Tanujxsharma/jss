package com.jobsearch.authservice.service;

import com.jobsearch.authservice.dto.AuthResponse;
import com.jobsearch.authservice.dto.LoginRequest;
import com.jobsearch.authservice.dto.RegisterRequest;
import com.jobsearch.authservice.entity.User;
import com.jobsearch.authservice.enums.Role;
import com.jobsearch.authservice.exception.DuplicateEmailException;
import com.jobsearch.authservice.exception.InvalidCredentialsException;
import com.jobsearch.authservice.repository.UserRepository;
import com.jobsearch.authservice.security.JwtUtil;
import com.jobsearch.authservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("johndoe")
                .email("john@example.com")
                .password("encoded_secret_password")
                .role(Role.JOB_SEEKER)
                .createdAt(Timestamp.from(Instant.now()))
                .build();

        registerRequest = RegisterRequest.builder()
                .username("johndoe")
                .email("john@example.com")
                .password("plain_password")
                .role(Role.JOB_SEEKER)
                .build();

        loginRequest = LoginRequest.builder()
                .email("john@example.com")
                .password("plain_password")
                .build();
    }

    @Test
    @DisplayName("Register user successfully returns AuthResponse with token")
    void register_Success() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plain_password")).thenReturn("encoded_secret_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtUtil.generateToken(any(UserDetails.class), eq(1L), eq(Role.JOB_SEEKER.name())))
                .thenReturn("mocked.jwt.token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("JOB_SEEKER", response.getRole());
        assertEquals("User registered successfully", response.getMessage());

        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordEncoder).encode("plain_password");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(any(UserDetails.class), eq(1L), eq(Role.JOB_SEEKER.name()));
    }

    @Test
    @DisplayName("Register with duplicate email throws DuplicateEmailException")
    void register_DuplicateEmail_ThrowsDuplicateEmailException() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals("Email is already registered: john@example.com", exception.getMessage());
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(jwtUtil, never()).generateToken(any(UserDetails.class), anyLong(), anyString());
    }

    @Test
    @DisplayName("Login with valid credentials successfully returns AuthResponse with token")
    void login_Success_ReturnsToken() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("plain_password", "encoded_secret_password")).thenReturn(true);
        when(jwtUtil.generateToken(any(UserDetails.class), eq(1L), eq(Role.JOB_SEEKER.name())))
                .thenReturn("mocked.jwt.token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("JOB_SEEKER", response.getRole());
        assertEquals("User logged in successfully", response.getMessage());

        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches("plain_password", "encoded_secret_password");
        verify(jwtUtil).generateToken(any(UserDetails.class), eq(1L), eq(Role.JOB_SEEKER.name()));
    }

    @Test
    @DisplayName("Login with wrong password throws InvalidCredentialsException")
    void login_WrongPassword_ThrowsInvalidCredentialsException() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("wrong_password", "encoded_secret_password")).thenReturn(false);

        LoginRequest wrongPasswordRequest = LoginRequest.builder()
                .email("john@example.com")
                .password("wrong_password")
                .build();

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(wrongPasswordRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches("wrong_password", "encoded_secret_password");
        verify(jwtUtil, never()).generateToken(any(UserDetails.class), anyLong(), anyString());
    }

    @Test
    @DisplayName("Login with non-existent email throws InvalidCredentialsException")
    void login_UserNotFound_ThrowsInvalidCredentialsException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        LoginRequest unknownUserRequest = LoginRequest.builder()
                .email("unknown@example.com")
                .password("plain_password")
                .build();

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(unknownUserRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail("unknown@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(any(UserDetails.class), anyLong(), anyString());
    }
}
