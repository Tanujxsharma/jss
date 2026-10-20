package com.jobsearch.applicationservice.controller;

import com.jobsearch.applicationservice.dto.ApplicationRequest;
import com.jobsearch.applicationservice.dto.ApplicationResponse;
import com.jobsearch.applicationservice.dto.StatusUpdateRequest;
import com.jobsearch.applicationservice.service.ApplicationService;
import com.jobsearch.applicationservice.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Application Management", description = "Endpoints for submitting and managing job applications and status workflows")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Apply for a job", description = "Submits a new job application after validating job and job seeker existence")
    public ResponseEntity<ApplicationResponse> applyForJob(
            @Valid @RequestBody ApplicationRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        ApplicationResponse response = applicationService.applyForJob(request, authorization, currentUserId(authorization));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/job/{jobId}")
    @Operation(summary = "Get applications by job ID", description = "Retrieves all job applications submitted for a specific job")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByJob(
            @Parameter(description = "ID of the job") @PathVariable Long jobId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByJob(jobId, currentUserId(authorization));
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/seeker/{seekerId}")
    @Operation(summary = "Get applications by seeker ID", description = "Retrieves all applications submitted by a specific job seeker")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsBySeeker(
            @Parameter(description = "ID of the job seeker") @PathVariable Long seekerId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        List<ApplicationResponse> responses = applicationService.getApplicationsBySeeker(seekerId, currentUserId(authorization));
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/employer/{employerId}")
    @Operation(summary = "Get applications by employer ID", description = "Retrieves all applications across jobs owned by a specific employer")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByEmployer(
            @Parameter(description = "ID of the employer") @PathVariable Long employerId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByEmployer(employerId, currentUserId(authorization));
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update application status", description = "Updates status of an application (e.g. SHORTLISTED, REJECTED, HIRED)")
    public ResponseEntity<ApplicationResponse> updateApplicationStatus(
            @Parameter(description = "ID of the application") @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        ApplicationResponse response = applicationService.updateApplicationStatus(
                id, request.getStatus(), currentUserId(authorization));
        return ResponseEntity.ok(response);
    }

    private Long currentUserId(String authorization) {
        return jwtUtil.extractUserId(authorization.substring("Bearer ".length()));
    }
}
