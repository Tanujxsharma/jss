package com.jobsearch.jobservice.controller;

import com.jobsearch.jobservice.dto.JobRequest;
import com.jobsearch.jobservice.dto.JobResponse;
import com.jobsearch.jobservice.service.JobService;
import com.jobsearch.jobservice.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Management", description = "APIs for managing job postings, searches, and employer listings")
public class JobController {

    private final JobService jobService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Create a new job posting", description = "Creates a new job with ACTIVE status and returns created job details")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request,
                                                 @RequestHeader("Authorization") String authorization) {
        JobResponse response = jobService.createJob(request, currentUserId(authorization), authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Search active jobs by filters", description = "Retrieves active jobs optionally filtered by title, location, skills, and company name")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<JobResponse>> searchJobs(
            @Parameter(description = "Job title to filter by") @RequestParam(required = false) String title,
            @Parameter(description = "Location to filter by") @RequestParam(required = false) String location,
            @Parameter(description = "Skills to filter by") @RequestParam(required = false) String skills,
            @Parameter(description = "Company name to filter by") @RequestParam(required = false) String company) {
        List<JobResponse> response = jobService.searchJobs(title, location, skills, company);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job details by ID", description = "Retrieves a single job posting by its unique ID")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JobResponse> getJobById(
            @Parameter(description = "Unique identifier of the job") @PathVariable Long id) {
        JobResponse response = jobService.getJobById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employer/{employerId}")
    @Operation(summary = "Get jobs posted by employer", description = "Retrieves all job postings for a specific employer")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<JobResponse>> getJobsByEmployer(
            @Parameter(description = "Employer ID") @PathVariable Long employerId) {
        List<JobResponse> response = jobService.getJobsByEmployer(employerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing job posting", description = "Updates fields of an existing job posting by ID")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JobResponse> updateJob(
            @Parameter(description = "Unique identifier of the job") @PathVariable Long id,
            @Valid @RequestBody JobRequest request,
            @RequestHeader("Authorization") String authorization) {
        JobResponse response = jobService.updateJob(id, request, currentUserId(authorization));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job posting", description = "Permanently deletes a job posting by ID")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteJob(
            @Parameter(description = "Unique identifier of the job") @PathVariable Long id,
            @RequestHeader("Authorization") String authorization) {
        jobService.deleteJob(id, currentUserId(authorization));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Close a job posting", description = "Marks a job posting as CLOSED so it no longer appears in active searches")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JobResponse> closeJob(
            @Parameter(description = "Unique identifier of the job") @PathVariable Long id,
            @RequestHeader("Authorization") String authorization) {
        JobResponse response = jobService.closeJob(id, currentUserId(authorization));
        return ResponseEntity.ok(response);
    }

    private Long currentUserId(String authorization) {
        return jwtUtil.extractUserId(authorization.substring("Bearer ".length()));
    }
}
