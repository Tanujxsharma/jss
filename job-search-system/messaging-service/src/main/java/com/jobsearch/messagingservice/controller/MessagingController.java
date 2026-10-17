package com.jobsearch.messagingservice.controller;

import com.jobsearch.messagingservice.dto.EmployerMessageRequest;
import com.jobsearch.messagingservice.dto.EmployerMessageResponse;
import com.jobsearch.messagingservice.dto.JobSeekerMessageRequest;
import com.jobsearch.messagingservice.dto.JobSeekerMessageResponse;
import com.jobsearch.messagingservice.service.MessagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messaging", description = "Endpoints for messaging between employers and job seekers")
@SecurityRequirement(name = "bearerAuth")
public class MessagingController {

    private final MessagingService messagingService;

    @PostMapping("/employer")
    @Operation(summary = "Send employer message", description = "Allows an employer to send a message to a job seeker regarding a specific job.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employer message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<EmployerMessageResponse> sendEmployerMessage(
            @Valid @RequestBody EmployerMessageRequest request) {
        EmployerMessageResponse response = messagingService.sendEmployerMessage(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/seeker")
    @Operation(summary = "Send job seeker message", description = "Allows a job seeker to send a message to an employer regarding a specific job.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Job seeker message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<JobSeekerMessageResponse> sendJobSeekerMessage(
            @Valid @RequestBody JobSeekerMessageRequest request) {
        JobSeekerMessageResponse response = messagingService.sendJobSeekerMessage(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/employer/{employerId}")
    @Operation(summary = "Get messages sent by employer", description = "Retrieves all messages sent by the specified employer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employer messages retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<EmployerMessageResponse>> getEmployerMessages(
            @Parameter(description = "ID of the employer", required = true)
            @PathVariable Long employerId) {
        List<EmployerMessageResponse> messages = messagingService.getEmployerMessages(employerId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/seeker/{seekerId}")
    @Operation(summary = "Get messages sent by job seeker", description = "Retrieves all messages sent by the specified job seeker.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job seeker messages retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<JobSeekerMessageResponse>> getJobSeekerMessages(
            @Parameter(description = "ID of the job seeker", required = true)
            @PathVariable("seekerId") Long seekerId) {
        List<JobSeekerMessageResponse> messages = messagingService.getJobSeekerMessages(seekerId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversation")
    @Operation(summary = "Get conversation between employer and job seeker",
            description = "Retrieves conversation messages between an employer and a job seeker for a given job.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Map<String, Object>> getConversation(
            @Parameter(description = "ID of the job opening", required = true)
            @RequestParam("jobId") Long jobId,
            @Parameter(description = "ID of the job seeker", required = true)
            @RequestParam("jobSeekerId") Long jobSeekerId,
            @Parameter(description = "ID of the employer", required = true)
            @RequestParam("employerId") Long employerId) {
        Map<String, Object> conversation = messagingService.getConversation(jobId, jobSeekerId, employerId);
        return ResponseEntity.ok(conversation);
    }
}
