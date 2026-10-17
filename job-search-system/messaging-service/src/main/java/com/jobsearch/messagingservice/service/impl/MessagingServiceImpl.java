package com.jobsearch.messagingservice.service.impl;

import com.jobsearch.messagingservice.dto.EmployerMessageRequest;
import com.jobsearch.messagingservice.dto.EmployerMessageResponse;
import com.jobsearch.messagingservice.dto.JobSeekerMessageRequest;
import com.jobsearch.messagingservice.dto.JobSeekerMessageResponse;
import com.jobsearch.messagingservice.entity.EmployerMessage;
import com.jobsearch.messagingservice.entity.JobSeekerMessage;
import com.jobsearch.messagingservice.repository.EmployerMessageRepository;
import com.jobsearch.messagingservice.repository.JobSeekerMessageRepository;
import com.jobsearch.messagingservice.service.MessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingServiceImpl implements MessagingService {

    private final EmployerMessageRepository employerMessageRepository;
    private final JobSeekerMessageRepository jobSeekerMessageRepository;

    @Override
    @Transactional
    public EmployerMessageResponse sendEmployerMessage(EmployerMessageRequest request) {
        log.info("Employer {} sending message to seeker {} for job {}",
                request.getEmployerId(), request.getJobSeekerId(), request.getJobId());

        EmployerMessage employerMessage = EmployerMessage.builder()
                .employerId(request.getEmployerId())
                .jobSeekerId(request.getJobSeekerId())
                .jobId(request.getJobId())
                .subject(request.getSubject())
                .messageDescription(request.getMessageDescription())
                .build();

        EmployerMessage saved = employerMessageRepository.save(employerMessage);
        log.info("Saved employer message with ID {}", saved.getMessageId());
        return mapToEmployerResponse(saved);
    }

    @Override
    @Transactional
    public JobSeekerMessageResponse sendJobSeekerMessage(JobSeekerMessageRequest request) {
        log.info("Job seeker {} sending message to employer {} for job {}",
                request.getJobSeekerId(), request.getEmployerId(), request.getJobId());

        JobSeekerMessage jobSeekerMessage = JobSeekerMessage.builder()
                .employerId(request.getEmployerId())
                .jobSeekerId(request.getJobSeekerId())
                .jobId(request.getJobId())
                .subject(request.getSubject())
                .messageDescription(request.getMessageDescription())
                .build();

        JobSeekerMessage saved = jobSeekerMessageRepository.save(jobSeekerMessage);
        log.info("Saved job seeker message with ID {}", saved.getMessageId());
        return mapToJobSeekerResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployerMessageResponse> getEmployerMessages(Long employerId) {
        log.info("Fetching messages sent by employer {}", employerId);
        return employerMessageRepository.findByEmployerId(employerId).stream()
                .map(this::mapToEmployerResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSeekerMessageResponse> getJobSeekerMessages(Long jobSeekerId) {
        log.info("Fetching messages sent by job seeker {}", jobSeekerId);
        return jobSeekerMessageRepository.findByJobSeekerId(jobSeekerId).stream()
                .map(this::mapToJobSeekerResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getConversation(Long jobId, Long jobSeekerId, Long employerId) {
        log.info("Fetching conversation for job {}, seeker {}, employer {}", jobId, jobSeekerId, employerId);

        List<EmployerMessageResponse> employerMessages = employerMessageRepository
                .findByJobIdAndJobSeekerIdAndEmployerIdOrderBySentDateAsc(jobId, jobSeekerId, employerId)
                .stream()
                .map(this::mapToEmployerResponse)
                .toList();

        List<JobSeekerMessageResponse> seekerMessages = jobSeekerMessageRepository
                .findByJobIdAndJobSeekerIdAndEmployerIdOrderBySentDateAsc(jobId, jobSeekerId, employerId)
                .stream()
                .map(this::mapToJobSeekerResponse)
                .toList();

        // Create unified combined chronological list of messages
        List<Map<String, Object>> combinedMessages = new ArrayList<>();
        employerMessages.forEach(msg -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("senderType", "EMPLOYER");
            item.put("messageId", msg.getMessageId());
            item.put("employerId", msg.getEmployerId());
            item.put("jobSeekerId", msg.getJobSeekerId());
            item.put("jobId", msg.getJobId());
            item.put("subject", msg.getSubject());
            item.put("messageDescription", msg.getMessageDescription());
            item.put("sentDate", msg.getSentDate());
            combinedMessages.add(item);
        });

        seekerMessages.forEach(msg -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("senderType", "JOB_SEEKER");
            item.put("messageId", msg.getMessageId());
            item.put("employerId", msg.getEmployerId());
            item.put("jobSeekerId", msg.getJobSeekerId());
            item.put("jobId", msg.getJobId());
            item.put("subject", msg.getSubject());
            item.put("messageDescription", msg.getMessageDescription());
            item.put("sentDate", msg.getSentDate());
            combinedMessages.add(item);
        });

        combinedMessages.sort(Comparator.comparing(
                m -> (Comparable) m.get("sentDate"),
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        Map<String, Object> conversation = new LinkedHashMap<>();
        conversation.put("employerMessages", employerMessages);
        conversation.put("seekerMessages", seekerMessages);
        conversation.put("combinedMessages", combinedMessages);

        return conversation;
    }

    private EmployerMessageResponse mapToEmployerResponse(EmployerMessage entity) {
        return EmployerMessageResponse.builder()
                .messageId(entity.getMessageId())
                .employerId(entity.getEmployerId())
                .jobSeekerId(entity.getJobSeekerId())
                .jobId(entity.getJobId())
                .subject(entity.getSubject())
                .messageDescription(entity.getMessageDescription())
                .sentDate(entity.getSentDate())
                .build();
    }

    private JobSeekerMessageResponse mapToJobSeekerResponse(JobSeekerMessage entity) {
        return JobSeekerMessageResponse.builder()
                .messageId(entity.getMessageId())
                .employerId(entity.getEmployerId())
                .jobSeekerId(entity.getJobSeekerId())
                .jobId(entity.getJobId())
                .subject(entity.getSubject())
                .messageDescription(entity.getMessageDescription())
                .sentDate(entity.getSentDate())
                .build();
    }
}
