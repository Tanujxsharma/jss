package com.jobsearch.jobseekerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobsearch.jobseekerservice.dto.JobSeekerRequest;
import com.jobsearch.jobseekerservice.dto.JobSeekerResponse;
import com.jobsearch.jobseekerservice.security.JwtUtil;
import com.jobsearch.jobseekerservice.service.JobSeekerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = JobSeekerController.class)
@AutoConfigureMockMvc(addFilters = false)
class JobSeekerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobSeekerService jobSeekerService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("GET /api/jobseekers/{id} - should return 200 and JobSeeker details")
    void getJobSeekerById_Returns200() throws Exception {
        JobSeekerResponse response = JobSeekerResponse.builder()
                .id(1L)
                .userId(101L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .location("New York")
                .skills("Java, Spring Boot")
                .experienceYears(new BigDecimal("5.0"))
                .summary("Experienced Developer")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(jobSeekerService.getJobSeekerById(eq(1L))).thenReturn(response);
        when(jwtUtil.extractRole("test-token")).thenReturn("JOB_SEEKER");
        when(jwtUtil.extractUserId("test-token")).thenReturn(101L);

        mockMvc.perform(get("/api/jobseekers/1")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("POST /api/jobseekers - valid request body should return 201 Created")
    void createJobSeeker_ValidBody_Returns201() throws Exception {
        JobSeekerRequest request = JobSeekerRequest.builder()
                .userId(101L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .location("New York")
                .skills("Java, Spring Boot")
                .experienceYears(new BigDecimal("5.0"))
                .summary("Experienced Developer")
                .build();

        JobSeekerResponse response = JobSeekerResponse.builder()
                .id(1L)
                .userId(101L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .location("New York")
                .skills("Java, Spring Boot")
                .experienceYears(new BigDecimal("5.0"))
                .summary("Experienced Developer")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(jwtUtil.extractUserId("test-token")).thenReturn(101L);
        when(jobSeekerService.createJobSeeker(any(JobSeekerRequest.class), eq(101L))).thenReturn(response);

        mockMvc.perform(post("/api/jobseekers")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("POST /api/jobseekers - blank name should return 400 Bad Request")
    void createJobSeeker_BlankName_Returns400() throws Exception {
        when(jwtUtil.extractUserId("test-token")).thenReturn(101L);
        JobSeekerRequest invalidRequest = JobSeekerRequest.builder()
                .userId(101L)
                .fullName("") // Blank name
                .email("john.doe@example.com")
                .skills("Java, Spring Boot")
                .experienceYears(new BigDecimal("5.0"))
                .build();

        mockMvc.perform(post("/api/jobseekers")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.fullName").exists());
    }
}
