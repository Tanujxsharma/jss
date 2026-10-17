package com.jobsearch.messagingservice.service;

import com.jobsearch.messagingservice.dto.EmployerMessageRequest;
import com.jobsearch.messagingservice.dto.EmployerMessageResponse;
import com.jobsearch.messagingservice.dto.JobSeekerMessageRequest;
import com.jobsearch.messagingservice.dto.JobSeekerMessageResponse;
import com.jobsearch.messagingservice.entity.EmployerMessage;
import com.jobsearch.messagingservice.entity.JobSeekerMessage;
import com.jobsearch.messagingservice.repository.EmployerMessageRepository;
import com.jobsearch.messagingservice.repository.JobSeekerMessageRepository;
import com.jobsearch.messagingservice.service.impl.MessagingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagingServiceImplTest {

    @Mock
    private EmployerMessageRepository employerMessageRepository;

    @Mock
    private JobSeekerMessageRepository jobSeekerMessageRepository;

    @InjectMocks
    private MessagingServiceImpl messagingService;

    private EmployerMessage sampleEmployerMessage;
    private JobSeekerMessage sampleJobSeekerMessage;
    private EmployerMessageRequest sampleEmployerRequest;
    private JobSeekerMessageRequest sampleJobSeekerRequest;

    @BeforeEach
    void setUp() {
        sampleEmployerMessage = EmployerMessage.builder()
                .messageId(1L)
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Interview Offer")
                .messageDescription("We would like to invite you for an interview.")
                .sentDate(LocalDateTime.now().minusHours(2))
                .build();

        sampleJobSeekerMessage = JobSeekerMessage.builder()
                .messageId(2L)
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Interview Confirmation")
                .messageDescription("Thank you! I am available on Monday.")
                .sentDate(LocalDateTime.now().minusHours(1))
                .build();

        sampleEmployerRequest = EmployerMessageRequest.builder()
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Interview Offer")
                .messageDescription("We would like to invite you for an interview.")
                .build();

        sampleJobSeekerRequest = JobSeekerMessageRequest.builder()
                .employerId(10L)
                .jobSeekerId(101L)
                .jobId(500L)
                .subject("Interview Confirmation")
                .messageDescription("Thank you! I am available on Monday.")
                .build();
    }

    @Test
    @DisplayName("sendEmployerMessage - success saves and returns employer message response")
    void testSendEmployerMessage_Success() {
        when(employerMessageRepository.save(any(EmployerMessage.class))).thenReturn(sampleEmployerMessage);

        EmployerMessageResponse response = messagingService.sendEmployerMessage(sampleEmployerRequest);

        assertNotNull(response);
        assertEquals(1L, response.getMessageId());
        assertEquals(10L, response.getEmployerId());
        assertEquals(101L, response.getJobSeekerId());
        assertEquals(500L, response.getJobId());
        assertEquals("Interview Offer", response.getSubject());
        assertEquals("We would like to invite you for an interview.", response.getMessageDescription());
        assertNotNull(response.getSentDate());

        verify(employerMessageRepository, times(1)).save(any(EmployerMessage.class));
    }

    @Test
    @DisplayName("sendJobSeekerMessage - success saves and returns job seeker message response")
    void testSendJobSeekerMessage_Success() {
        when(jobSeekerMessageRepository.save(any(JobSeekerMessage.class))).thenReturn(sampleJobSeekerMessage);

        JobSeekerMessageResponse response = messagingService.sendJobSeekerMessage(sampleJobSeekerRequest);

        assertNotNull(response);
        assertEquals(2L, response.getMessageId());
        assertEquals(10L, response.getEmployerId());
        assertEquals(101L, response.getJobSeekerId());
        assertEquals(500L, response.getJobId());
        assertEquals("Interview Confirmation", response.getSubject());
        assertEquals("Thank you! I am available on Monday.", response.getMessageDescription());
        assertNotNull(response.getSentDate());

        verify(jobSeekerMessageRepository, times(1)).save(any(JobSeekerMessage.class));
    }

    @Test
    @DisplayName("getConversation - returns messages from both sides")
    void testGetConversation_ReturnsBothSides() {
        when(employerMessageRepository.findByJobIdAndJobSeekerIdAndEmployerIdOrderBySentDateAsc(500L, 101L, 10L))
                .thenReturn(List.of(sampleEmployerMessage));
        when(jobSeekerMessageRepository.findByJobIdAndJobSeekerIdAndEmployerIdOrderBySentDateAsc(500L, 101L, 10L))
                .thenReturn(List.of(sampleJobSeekerMessage));

        Map<String, Object> conversation = messagingService.getConversation(500L, 101L, 10L);

        assertNotNull(conversation);
        assertTrue(conversation.containsKey("employerMessages"));
        assertTrue(conversation.containsKey("seekerMessages"));

        @SuppressWarnings("unchecked")
        List<EmployerMessageResponse> employerMessages =
                (List<EmployerMessageResponse>) conversation.get("employerMessages");
        @SuppressWarnings("unchecked")
        List<JobSeekerMessageResponse> seekerMessages =
                (List<JobSeekerMessageResponse>) conversation.get("seekerMessages");

        assertEquals(1, employerMessages.size());
        assertEquals(1, seekerMessages.size());
        assertEquals("Interview Offer", employerMessages.get(0).getSubject());
        assertEquals("Interview Confirmation", seekerMessages.get(0).getSubject());

        verify(employerMessageRepository, times(1))
                .findByJobIdAndJobSeekerIdAndEmployerIdOrderBySentDateAsc(500L, 101L, 10L);
        verify(jobSeekerMessageRepository, times(1))
                .findByJobIdAndJobSeekerIdAndEmployerIdOrderBySentDateAsc(500L, 101L, 10L);
    }

    @Test
    @DisplayName("getEmployerMessages - returns list of messages for employer")
    void testGetEmployerMessages_Success() {
        when(employerMessageRepository.findByEmployerId(10L)).thenReturn(List.of(sampleEmployerMessage));

        List<EmployerMessageResponse> responses = messagingService.getEmployerMessages(10L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getMessageId());
        verify(employerMessageRepository, times(1)).findByEmployerId(10L);
    }

    @Test
    @DisplayName("getJobSeekerMessages - returns list of messages for job seeker")
    void testGetJobSeekerMessages_Success() {
        when(jobSeekerMessageRepository.findByJobSeekerId(101L)).thenReturn(List.of(sampleJobSeekerMessage));

        List<JobSeekerMessageResponse> responses = messagingService.getJobSeekerMessages(101L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(2L, responses.get(0).getMessageId());
        verify(jobSeekerMessageRepository, times(1)).findByJobSeekerId(101L);
    }
}
