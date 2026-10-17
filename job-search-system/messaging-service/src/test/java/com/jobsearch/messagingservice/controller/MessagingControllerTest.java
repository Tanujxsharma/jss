package com.jobsearch.messagingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobsearch.messagingservice.dto.EmployerMessageRequest;
import com.jobsearch.messagingservice.dto.EmployerMessageResponse;
import com.jobsearch.messagingservice.dto.JobSeekerMessageRequest;
import com.jobsearch.messagingservice.dto.JobSeekerMessageResponse;
import com.jobsearch.messagingservice.security.JwtAuthenticationFilter;
import com.jobsearch.messagingservice.security.JwtUtil;
import com.jobsearch.messagingservice.service.MessagingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessagingController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessagingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessagingService messagingService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /api/messages/employer - success returns 201 Created")
    void testSendEmployerMessage_Success() throws Exception {
        EmployerMessageRequest request = EmployerMessageRequest.builder()
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Interview Offer")
                .messageDescription("We would like to invite you for an interview.")
                .build();

        EmployerMessageResponse response = EmployerMessageResponse.builder()
                .messageId(1L)
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Interview Offer")
                .messageDescription("We would like to invite you for an interview.")
                .sentDate(LocalDateTime.now())
                .build();

        when(messagingService.sendEmployerMessage(any(EmployerMessageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/messages/employer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageId").value(1L))
                .andExpect(jsonPath("$.employerId").value(10L))
                .andExpect(jsonPath("$.jobSeekerId").value(101L))
                .andExpect(jsonPath("$.subject").value("Interview Offer"));
    }

    @Test
    @DisplayName("POST /api/messages/employer - validation failure returns 400 Bad Request")
    void testSendEmployerMessage_ValidationFailure() throws Exception {
        EmployerMessageRequest invalidRequest = EmployerMessageRequest.builder()
                .employerId(null)
                .jobSeekerId(null)
                .jobId(null)
                .subject("")
                .messageDescription("")
                .build();

        mockMvc.perform(post("/api/messages/employer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.employerId").exists())
                .andExpect(jsonPath("$.validationErrors.jobSeekerId").exists())
                .andExpect(jsonPath("$.validationErrors.subject").exists());
    }

    @Test
    @DisplayName("POST /api/messages/seeker - success returns 201 Created")
    void testSendJobSeekerMessage_Success() throws Exception {
        JobSeekerMessageRequest request = JobSeekerMessageRequest.builder()
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Interview Confirmation")
                .messageDescription("Thank you! I will be there.")
                .build();

        JobSeekerMessageResponse response = JobSeekerMessageResponse.builder()
                .messageId(2L)
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Interview Confirmation")
                .messageDescription("Thank you! I will be there.")
                .sentDate(LocalDateTime.now())
                .build();

        when(messagingService.sendJobSeekerMessage(any(JobSeekerMessageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/messages/seeker")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageId").value(2L))
                .andExpect(jsonPath("$.jobSeekerId").value(101L))
                .andExpect(jsonPath("$.subject").value("Interview Confirmation"));
    }

    @Test
    @DisplayName("GET /api/messages/employer/{employerId} - returns 200 OK")
    void testGetEmployerMessages_Success() throws Exception {
        EmployerMessageResponse response = EmployerMessageResponse.builder()
                .messageId(1L)
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Interview Offer")
                .messageDescription("Details inside.")
                .sentDate(LocalDateTime.now())
                .build();

        when(messagingService.getEmployerMessages(10L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/messages/employer/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].messageId").value(1L))
                .andExpect(jsonPath("$[0].employerId").value(10L));
    }

    @Test
    @DisplayName("GET /api/messages/seeker/{seekerId} - returns 200 OK")
    void testGetJobSeekerMessages_Success() throws Exception {
        JobSeekerMessageResponse response = JobSeekerMessageResponse.builder()
                .messageId(2L)
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Interview Confirmation")
                .messageDescription("Details inside.")
                .sentDate(LocalDateTime.now())
                .build();

        when(messagingService.getJobSeekerMessages(101L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/messages/seeker/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].messageId").value(2L))
                .andExpect(jsonPath("$[0].jobSeekerId").value(101L));
    }

    @Test
    @DisplayName("GET /api/messages/conversation - returns 200 OK with both sides")
    void testGetConversation_Success() throws Exception {
        EmployerMessageResponse empMsg = EmployerMessageResponse.builder()
                .messageId(1L)
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Offer")
                .messageDescription("Offer details")
                .sentDate(LocalDateTime.now().minusHours(1))
                .build();

        JobSeekerMessageResponse seekerMsg = JobSeekerMessageResponse.builder()
                .messageId(2L)
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Response")
                .messageDescription("Response details")
                .sentDate(LocalDateTime.now())
                .build();

        Map<String, Object> conversation = new LinkedHashMap<>();
        conversation.put("employerMessages", List.of(empMsg));
        conversation.put("seekerMessages", List.of(seekerMsg));

        when(messagingService.getConversation(500L, 101L, 10L)).thenReturn(conversation);

        mockMvc.perform(get("/api/messages/conversation")
                        .param("jobId", "500")
                        .param("jobSeekerId", "101")
                        .param("employerId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employerMessages[0].messageId").value(1L))
                .andExpect(jsonPath("$.seekerMessages[0].messageId").value(2L));
    }
}
