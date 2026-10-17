package com.jobsearch.messagingservice.service;

import com.jobsearch.messagingservice.dto.ConversationRequest;
import com.jobsearch.messagingservice.dto.EmployerMessageRequest;
import com.jobsearch.messagingservice.dto.EmployerMessageResponse;
import com.jobsearch.messagingservice.dto.JobSeekerMessageRequest;
import com.jobsearch.messagingservice.dto.JobSeekerMessageResponse;

import java.util.List;
import java.util.Map;

public interface MessagingService {

    EmployerMessageResponse sendEmployerMessage(EmployerMessageRequest request);

    JobSeekerMessageResponse sendJobSeekerMessage(JobSeekerMessageRequest request);

    List<EmployerMessageResponse> getEmployerMessages(Long employerId);

    List<JobSeekerMessageResponse> getJobSeekerMessages(Long jobSeekerId);

    Map<String, Object> getConversation(Long jobId, Long jobSeekerId, Long employerId);

    default Map<String, Object> getConversation(ConversationRequest request) {
        return getConversation(request.getJobId(), request.getJobSeekerId(), request.getEmployerId());
    }
}
