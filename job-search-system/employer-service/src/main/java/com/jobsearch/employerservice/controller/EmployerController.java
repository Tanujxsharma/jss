package com.jobsearch.employerservice.controller;

import com.jobsearch.employerservice.dto.EmployerRequest;
import com.jobsearch.employerservice.dto.EmployerResponse;
import com.jobsearch.employerservice.service.EmployerService;
import com.jobsearch.employerservice.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
@Tag(name = "Employer Management", description = "Endpoints for managing employer profiles")
@SecurityRequirement(name = "bearerAuth")
public class EmployerController {

    private final EmployerService employerService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Create employer profile", description = "Creates a new employer profile and returns the created record")
    public ResponseEntity<EmployerResponse> createEmployer(
            @Valid @RequestBody EmployerRequest request,
            @RequestHeader("Authorization") String authorization) {
        EmployerResponse response = employerService.createEmployer(request, currentUserId(authorization));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employer profile by ID", description = "Retrieves an employer profile by its unique identifier")
    public ResponseEntity<EmployerResponse> getEmployerById(@PathVariable Long id) {
        EmployerResponse response = employerService.getEmployerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get employer profile by user ID", description = "Retrieves an employer profile by the associated auth user ID")
    public ResponseEntity<EmployerResponse> getEmployerByUserId(@PathVariable Long userId) {
        EmployerResponse response = employerService.getEmployerByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all employers", description = "Retrieves a list of all employer profiles")
    public ResponseEntity<List<EmployerResponse>> getAllEmployers() {
        List<EmployerResponse> responses = employerService.getAllEmployers();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employer profile", description = "Updates an existing employer profile by its ID")
    public ResponseEntity<EmployerResponse> updateEmployer(
            @PathVariable Long id,
            @Valid @RequestBody EmployerRequest request,
            @RequestHeader("Authorization") String authorization) {
        EmployerResponse response = employerService.updateEmployer(id, request, currentUserId(authorization));
        return ResponseEntity.ok(response);
    }

    private Long currentUserId(String authorization) {
        return jwtUtil.extractUserId(authorization.substring("Bearer ".length()));
    }
}
