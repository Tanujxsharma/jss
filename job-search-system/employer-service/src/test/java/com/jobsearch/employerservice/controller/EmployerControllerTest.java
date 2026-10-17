package com.jobsearch.employerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobsearch.employerservice.dto.EmployerRequest;
import com.jobsearch.employerservice.dto.EmployerResponse;
import com.jobsearch.employerservice.exception.EmployerNotFoundException;
import com.jobsearch.employerservice.security.JwtAuthenticationFilter;
import com.jobsearch.employerservice.security.JwtUtil;
import com.jobsearch.employerservice.service.EmployerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployerController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployerService employerService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private EmployerRequest validRequest;
    private EmployerResponse sampleResponse;

    @BeforeEach
    void setUp() {
        validRequest = EmployerRequest.builder()
                .userId(101L)
                .organizationName("Acme Corp")
                .email("hr@acme.com")
                .contactNumber("9876543210")
                .location("San Francisco, CA")
                .about("Innovative tech company")
                .website("https://acme.com")
                .build();

        sampleResponse = EmployerResponse.builder()
                .id(1L)
                .userId(101L)
                .organizationName("Acme Corp")
                .email("hr@acme.com")
                .contactNumber("9876543210")
                .location("San Francisco, CA")
                .about("Innovative tech company")
                .website("https://acme.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/employers returns 200 and list of employers")
    void getAllEmployers_Returns200() throws Exception {
        when(employerService.getAllEmployers()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/employers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].organizationName").value("Acme Corp"))
                .andExpect(jsonPath("$[0].email").value("hr@acme.com"));
    }

    @Test
    @DisplayName("POST /api/employers with valid body returns 201 Created")
    void createEmployer_ValidBody_Returns201() throws Exception {
        when(employerService.createEmployer(any(EmployerRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/employers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.organizationName").value("Acme Corp"))
                .andExpect(jsonPath("$.email").value("hr@acme.com"));
    }

    @Test
    @DisplayName("POST /api/employers with blank org name returns 400 Bad Request")
    void createEmployer_BlankOrgName_Returns400() throws Exception {
        EmployerRequest invalidRequest = EmployerRequest.builder()
                .userId(101L)
                .organizationName("") // blank org name
                .email("hr@acme.com")
                .contactNumber("9876543210")
                .location("San Francisco, CA")
                .build();

        mockMvc.perform(post("/api/employers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.organizationName").exists());
    }

    @Test
    @DisplayName("GET /api/employers/{id} when not found returns 404 Not Found")
    void getEmployerById_NotFound_Returns404() throws Exception {
        when(employerService.getEmployerById(99L))
                .thenThrow(new EmployerNotFoundException("Employer not found with id: 99"));

        mockMvc.perform(get("/api/employers/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Employer not found with id: 99"));
    }

    @Test
    @DisplayName("GET /api/employers/{id} when found returns 200 OK")
    void getEmployerById_Success_Returns200() throws Exception {
        when(employerService.getEmployerById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/employers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.organizationName").value("Acme Corp"));
    }

    @Test
    @DisplayName("GET /api/employers/user/{userId} when found returns 200 OK")
    void getEmployerByUserId_Success_Returns200() throws Exception {
        when(employerService.getEmployerByUserId(101L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/employers/user/{userId}", 101L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(101L))
                .andExpect(jsonPath("$.organizationName").value("Acme Corp"));
    }

    @Test
    @DisplayName("PUT /api/employers/{id} with valid body returns 200 OK")
    void updateEmployer_Success_Returns200() throws Exception {
        when(employerService.updateEmployer(eq(1L), any(EmployerRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/employers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.organizationName").value("Acme Corp"));
    }
}
