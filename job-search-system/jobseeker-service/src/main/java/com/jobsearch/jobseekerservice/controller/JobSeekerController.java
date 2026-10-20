package com.jobsearch.jobseekerservice.controller;

import com.jobsearch.jobseekerservice.dto.JobSeekerRequest;
import com.jobsearch.jobseekerservice.dto.JobSeekerResponse;
import com.jobsearch.jobseekerservice.service.JobSeekerService;
import com.jobsearch.jobseekerservice.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/jobseekers")
@RequiredArgsConstructor
@Tag(name = "JobSeeker Management", description = "Endpoints for managing job seeker profiles, resume uploads, and candidate searches")
@SecurityRequirement(name = "bearerAuth")
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Create a new job seeker profile")
    public ResponseEntity<JobSeekerResponse> createJobSeeker(
            @Valid @RequestBody JobSeekerRequest request,
            @RequestHeader("Authorization") String authorization) {
        JobSeekerResponse created = jobSeekerService.createJobSeeker(request, currentUserId(authorization));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job seeker profile by ID")
    public ResponseEntity<JobSeekerResponse> getJobSeekerById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization) {
        JobSeekerResponse response = jobSeekerService.getJobSeekerById(id);
        String role = jwtUtil.extractRole(authorization.substring("Bearer ".length()));
        Long userId = currentUserId(authorization);
        if (!"EMPLOYER".equals(role) && !userId.equals(response.getUserId())) {
            throw new AccessDeniedException("Cannot view another job seeker's profile");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get job seeker profile by auth User ID")
    public ResponseEntity<JobSeekerResponse> getJobSeekerByUserId(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authorization) {
        if (!currentUserId(authorization).equals(userId)) {
            throw new AccessDeniedException("Cannot view another job seeker's profile");
        }
        JobSeekerResponse response = jobSeekerService.getJobSeekerByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing job seeker profile")
    public ResponseEntity<JobSeekerResponse> updateJobSeeker(@PathVariable Long id,
                                                             @Valid @RequestBody JobSeekerRequest request,
                                                             @RequestHeader("Authorization") String authorization) {
        JobSeekerResponse updated = jobSeekerService.updateJobSeeker(id, request, currentUserId(authorization));
        return ResponseEntity.ok(updated);
    }

    @PostMapping(value = "/{id}/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload resume for a job seeker")
    public ResponseEntity<JobSeekerResponse> uploadResume(@PathVariable Long id,
                                                         @RequestParam("file") MultipartFile file,
                                                         @RequestHeader("Authorization") String authorization) {
        JobSeekerResponse updated = jobSeekerService.uploadResume(id, file, currentUserId(authorization));
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/search")
    @Operation(summary = "Search job seekers by skills, location, and minimum experience")
    public ResponseEntity<List<JobSeekerResponse>> searchCandidates(
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minExperience) {
        List<JobSeekerResponse> candidates = jobSeekerService.searchCandidates(skills, location, minExperience);
        return ResponseEntity.ok(candidates);
    }

    private Long currentUserId(String authorization) {
        return jwtUtil.extractUserId(authorization.substring("Bearer ".length()));
    }
}
