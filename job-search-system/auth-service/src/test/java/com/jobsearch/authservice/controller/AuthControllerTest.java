package com.jobsearch.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobsearch.authservice.dto.AuthResponse;
import com.jobsearch.authservice.dto.LoginRequest;
import com.jobsearch.authservice.dto.RegisterRequest;
import com.jobsearch.authservice.enums.Role;
import com.jobsearch.authservice.security.JwtUtil;
import com.jobsearch.authservice.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("POST /api/auth/register with valid body returns 201 Created")
    void register_ValidBody_Returns201() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("johndoe")
                .email("john@example.com")
                .password("securePassword123")
                .role(Role.JOB_SEEKER)
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("jwt.token.mock")
                .userId(1L)
                .email("john@example.com")
                .role("JOB_SEEKER")
                .message("User registered successfully")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt.token.mock"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("JOB_SEEKER"))
                .andExpect(jsonPath("$.message").value("User registered successfully"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/register with invalid email returns 400 Bad Request")
    void register_InvalidEmail_Returns400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("johndoe")
                .email("not-a-valid-email")
                .password("securePassword123")
                .role(Role.JOB_SEEKER)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login with valid credentials returns 200 OK")
    void login_ValidCredentials_Returns200() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("securePassword123")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("jwt.token.mock")
                .userId(1L)
                .email("john@example.com")
                .role("JOB_SEEKER")
                .message("User logged in successfully")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.mock"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("JOB_SEEKER"))
                .andExpect(jsonPath("$.message").value("User logged in successfully"));

        verify(authService).login(any(LoginRequest.class));
    }
}
