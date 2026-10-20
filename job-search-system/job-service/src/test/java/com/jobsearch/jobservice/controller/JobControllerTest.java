package com.jobsearch.jobservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobsearch.jobservice.dto.JobRequest;
import com.jobsearch.jobservice.dto.JobResponse;
import com.jobsearch.jobservice.enums.JobStatus;
import com.jobsearch.jobservice.exception.JobNotFoundException;
import com.jobsearch.jobservice.security.JwtAuthenticationFilter;
import com.jobsearch.jobservice.security.JwtUtil;
import com.jobsearch.jobservice.service.JobService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
@AutoConfigureMockMvc(addFilters = false)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobService jobService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private JobResponse createSampleResponse(Long id) {
        return JobResponse.builder()
                .id(id)
                .employerId(10L)
                .title("Software Engineer")
                .companyName("Acme Corp")
                .location("New York, NY")
                .description("Build scalable backend services")
                .skillsRequired("Java, Spring Boot")
                .experienceRequired(new BigDecimal("3.0"))
                .salaryRange("$100k-$130k")
                .jobType("FULL_TIME")
                .status(JobStatus.ACTIVE)
                .postedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/jobs returns 200 with list of jobs")
    @WithMockUser
    void searchJobs_Returns200() throws Exception {
        when(jobService.searchJobs(null, null, null, null))
                .thenReturn(List.of(createSampleResponse(1L)));

        mockMvc.perform(get("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Software Engineer")));

        verify(jobService, times(1)).searchJobs(null, null, null, null);
    }

    @Test
    @DisplayName("POST /api/jobs with valid body returns 201 Created")
    @WithMockUser
    void createJob_ValidBody_Returns201() throws Exception {
        JobRequest request = JobRequest.builder()
                .employerId(10L)
                .title("Software Engineer")
                .companyName("Acme Corp")
                .location("New York, NY")
                .description("Build scalable backend services")
                .skillsRequired("Java, Spring Boot")
                .experienceRequired(new BigDecimal("3.0"))
                .salaryRange("$100k-$130k")
                .jobType("FULL_TIME")
                .build();

        when(jwtUtil.extractUserId("test-token")).thenReturn(101L);
        when(jobService.createJob(any(JobRequest.class), eq(101L), eq("Bearer test-token")))
                .thenReturn(createSampleResponse(1L));

        mockMvc.perform(post("/api/jobs")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Software Engineer")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));

        verify(jobService, times(1)).createJob(any(JobRequest.class), eq(101L), eq("Bearer test-token"));
    }

    @Test
    @DisplayName("POST /api/jobs with blank title returns 400 Bad Request")
    @WithMockUser
    void createJob_BlankTitle_Returns400() throws Exception {
        JobRequest request = JobRequest.builder()
                .employerId(10L)
                .title("") // Blank title
                .skillsRequired("Java")
                .build();

        mockMvc.perform(post("/api/jobs")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.validationErrors.title", notNullValue()));

        verify(jobService, never()).createJob(any(JobRequest.class), any(), any());
    }

    @Test
    @DisplayName("GET /api/jobs/{id} when found returns 200")
    @WithMockUser
    void getJobById_Found_Returns200() throws Exception {
        when(jobService.getJobById(1L)).thenReturn(createSampleResponse(1L));

        mockMvc.perform(get("/api/jobs/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Software Engineer")));

        verify(jobService, times(1)).getJobById(1L);
    }

    @Test
    @DisplayName("GET /api/jobs/{id} when not found returns 404 Not Found")
    @WithMockUser
    void getJobById_NotFound_Returns404() throws Exception {
        when(jobService.getJobById(999L)).thenThrow(new JobNotFoundException(999L));

        mockMvc.perform(get("/api/jobs/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("999")));

        verify(jobService, times(1)).getJobById(999L);
    }

    @Test
    @DisplayName("GET /api/jobs/employer/{employerId} returns 200")
    @WithMockUser
    void getJobsByEmployer_Returns200() throws Exception {
        when(jobService.getJobsByEmployer(10L)).thenReturn(List.of(createSampleResponse(1L)));

        mockMvc.perform(get("/api/jobs/employer/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].employerId", is(10)));

        verify(jobService, times(1)).getJobsByEmployer(10L);
    }

    @Test
    @DisplayName("PUT /api/jobs/{id} with valid body returns 200")
    @WithMockUser
    void updateJob_ValidBody_Returns200() throws Exception {
        JobRequest request = JobRequest.builder()
                .employerId(10L)
                .title("Senior Software Engineer")
                .companyName("Acme Corp")
                .location("Remote")
                .description("Updated description")
                .skillsRequired("Java, Spring Boot, AWS")
                .experienceRequired(new BigDecimal("5.0"))
                .salaryRange("$120k-$150k")
                .jobType("FULL_TIME")
                .build();

        JobResponse updatedResponse = createSampleResponse(1L);
        updatedResponse.setTitle("Senior Software Engineer");

        when(jwtUtil.extractUserId("test-token")).thenReturn(101L);
        when(jobService.updateJob(eq(1L), any(JobRequest.class), eq(101L))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/jobs/1")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Senior Software Engineer")));

        verify(jobService, times(1)).updateJob(eq(1L), any(JobRequest.class), eq(101L));
    }

    @Test
    @DisplayName("DELETE /api/jobs/{id} returns 204 No Content")
    @WithMockUser
    void deleteJob_Returns204() throws Exception {
        when(jwtUtil.extractUserId("test-token")).thenReturn(101L);
        doNothing().when(jobService).deleteJob(1L, 101L);

        mockMvc.perform(delete("/api/jobs/1").header("Authorization", "Bearer test-token"))
                .andExpect(status().isNoContent());

        verify(jobService, times(1)).deleteJob(1L, 101L);
    }

    @Test
    @DisplayName("PATCH /api/jobs/{id}/close returns 200 OK")
    @WithMockUser
    void closeJob_Returns200() throws Exception {
        JobResponse closedResponse = createSampleResponse(1L);
        closedResponse.setStatus(JobStatus.CLOSED);

        when(jwtUtil.extractUserId("test-token")).thenReturn(101L);
        when(jobService.closeJob(1L, 101L)).thenReturn(closedResponse);

        mockMvc.perform(patch("/api/jobs/1/close").header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("CLOSED")));

        verify(jobService, times(1)).closeJob(1L, 101L);
    }
}
