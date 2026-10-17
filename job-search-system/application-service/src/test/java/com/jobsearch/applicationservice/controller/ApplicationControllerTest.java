package com.jobsearch.applicationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobsearch.applicationservice.dto.ApplicationRequest;
import com.jobsearch.applicationservice.dto.ApplicationResponse;
import com.jobsearch.applicationservice.dto.StatusUpdateRequest;
import com.jobsearch.applicationservice.enums.ApplicationStatus;
import com.jobsearch.applicationservice.exception.ApplicationNotFoundException;
import com.jobsearch.applicationservice.security.JwtAuthenticationFilter;
import com.jobsearch.applicationservice.security.JwtUtil;
import com.jobsearch.applicationservice.service.ApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ApplicationResponse createSampleResponse(Long id, ApplicationStatus status) {
        return ApplicationResponse.builder()
                .id(id)
                .jobId(100L)
                .jobSeekerId(200L)
                .employerId(300L)
                .coverLetter("Experienced engineer application")
                .status(status)
                .appliedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /api/applications with valid body returns 201 Created")
    @WithMockUser
    void applyForJob_ValidBody_Returns201() throws Exception {
        ApplicationRequest request = ApplicationRequest.builder()
                .jobId(100L)
                .jobSeekerId(200L)
                .employerId(300L)
                .coverLetter("Experienced engineer application")
                .build();

        when(applicationService.applyForJob(any(ApplicationRequest.class)))
                .thenReturn(createSampleResponse(1L, ApplicationStatus.PENDING));

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.jobId", is(100)))
                .andExpect(jsonPath("$.jobSeekerId", is(200)))
                .andExpect(jsonPath("$.employerId", is(300)))
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(applicationService, times(1)).applyForJob(any(ApplicationRequest.class));
    }

    @Test
    @DisplayName("POST /api/applications with missing required fields returns 400 Bad Request")
    @WithMockUser
    void applyForJob_InvalidBody_Returns400() throws Exception {
        ApplicationRequest invalidRequest = ApplicationRequest.builder()
                .jobId(null) // missing jobId
                .jobSeekerId(null) // missing jobSeekerId
                .employerId(null) // missing employerId
                .build();

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.errors.jobId", notNullValue()))
                .andExpect(jsonPath("$.errors.jobSeekerId", notNullValue()))
                .andExpect(jsonPath("$.errors.employerId", notNullValue()));

        verify(applicationService, never()).applyForJob(any());
    }

    @Test
    @DisplayName("GET /api/applications/job/{jobId} returns 200 with list of applications")
    @WithMockUser
    void getApplicationsByJob_Returns200() throws Exception {
        when(applicationService.getApplicationsByJob(100L))
                .thenReturn(List.of(createSampleResponse(1L, ApplicationStatus.PENDING)));

        mockMvc.perform(get("/api/applications/job/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].jobId", is(100)));

        verify(applicationService, times(1)).getApplicationsByJob(100L);
    }

    @Test
    @DisplayName("GET /api/applications/seeker/{seekerId} returns 200 with list of applications")
    @WithMockUser
    void getApplicationsBySeeker_Returns200() throws Exception {
        when(applicationService.getApplicationsBySeeker(200L))
                .thenReturn(List.of(createSampleResponse(1L, ApplicationStatus.PENDING)));

        mockMvc.perform(get("/api/applications/seeker/200")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].jobSeekerId", is(200)));

        verify(applicationService, times(1)).getApplicationsBySeeker(200L);
    }

    @Test
    @DisplayName("GET /api/applications/employer/{employerId} returns 200 with list of applications")
    @WithMockUser
    void getApplicationsByEmployer_Returns200() throws Exception {
        when(applicationService.getApplicationsByEmployer(300L))
                .thenReturn(List.of(createSampleResponse(1L, ApplicationStatus.PENDING)));

        mockMvc.perform(get("/api/applications/employer/300")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].employerId", is(300)));

        verify(applicationService, times(1)).getApplicationsByEmployer(300L);
    }

    @Test
    @DisplayName("GET /api/applications/{id} when found returns 200")
    @WithMockUser
    void getApplicationById_Found_Returns200() throws Exception {
        when(applicationService.getApplicationById(1L))
                .thenReturn(createSampleResponse(1L, ApplicationStatus.PENDING));

        mockMvc.perform(get("/api/applications/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(applicationService, times(1)).getApplicationById(1L);
    }

    @Test
    @DisplayName("GET /api/applications/{id} when not found returns 404")
    @WithMockUser
    void getApplicationById_NotFound_Returns404() throws Exception {
        when(applicationService.getApplicationById(999L))
                .thenThrow(new ApplicationNotFoundException(999L));

        mockMvc.perform(get("/api/applications/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("999")));

        verify(applicationService, times(1)).getApplicationById(999L);
    }

    @Test
    @DisplayName("PATCH /api/applications/{id}/status returns 200 with updated status")
    @WithMockUser
    void updateApplicationStatus_Returns200() throws Exception {
        StatusUpdateRequest updateRequest = StatusUpdateRequest.builder()
                .status(ApplicationStatus.SHORTLISTED)
                .build();

        when(applicationService.updateApplicationStatus(eq(1L), eq(ApplicationStatus.SHORTLISTED)))
                .thenReturn(createSampleResponse(1L, ApplicationStatus.SHORTLISTED));

        mockMvc.perform(patch("/api/applications/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("SHORTLISTED")));

        verify(applicationService, times(1)).updateApplicationStatus(1L, ApplicationStatus.SHORTLISTED);
    }

    @Test
    @DisplayName("PATCH /api/applications/{id}/status with null status returns 400 Bad Request")
    @WithMockUser
    void updateApplicationStatus_NullStatus_Returns400() throws Exception {
        StatusUpdateRequest invalidRequest = StatusUpdateRequest.builder()
                .status(null)
                .build();

        mockMvc.perform(patch("/api/applications/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.errors.status", notNullValue()));

        verify(applicationService, never()).updateApplicationStatus(any(), any());
    }
}
