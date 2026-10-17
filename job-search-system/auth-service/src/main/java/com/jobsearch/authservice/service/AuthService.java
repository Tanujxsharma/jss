package com.jobsearch.authservice.service;

import com.jobsearch.authservice.dto.AuthResponse;
import com.jobsearch.authservice.dto.LoginRequest;
import com.jobsearch.authservice.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
