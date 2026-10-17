package com.jobsearch.applicationservice.service.impl;

import com.jobsearch.applicationservice.client.JobSeekerServiceClient;
import com.jobsearch.applicationservice.client.JobServiceClient;
import com.jobsearch.applicationservice.dto.ApplicationRequest;
import com.jobsearch.applicationservice.dto.ApplicationResponse;
import com.jobsearch.applicationservice.entity.Application;
import com.jobsearch.applicationservice.enums.ApplicationStatus;
import com.jobsearch.applicationservice.exception.ApplicationNotFoundException;
import com.jobsearch.applicationservice.exception.DuplicateApplicationException;
import com.jobsearch.applicationservice.exception.JobNotFoundException;
import com.jobsearch.applicationservice.exception.JobSeekerNotFoundException;
import com.jobsearch.applicationservice.repository.ApplicationRepository;
import com.jobsearch.applicationservice.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobServiceClient jobServiceClient;
    private final JobSeekerServiceClient jobSeekerServiceClient;

    @Override
    @Transactional
    public ApplicationResponse applyForJob(ApplicationRequest request) {
        log.info("Processing application for jobId: {} by jobSeekerId: {}",
                request.getJobId(), request.getJobSeekerId());

        // 1. Check duplicate application
        if (applicationRepository.existsByJobIdAndJobSeekerId(request.getJobId(), request.getJobSeekerId())) {
            log.warn("Duplicate application detected for jobId: {} and jobSeekerId: {}",
                    request.getJobId(), request.getJobSeekerId());
            throw new DuplicateApplicationException(request.getJobId(), request.getJobSeekerId());
        }

        // 2. Validate job exists via JobServiceClient
        if (!jobServiceClient.validateJobExists(request.getJobId())) {
            log.warn("Job validation failed: job not found with id {}", request.getJobId());
            throw new JobNotFoundException(request.getJobId());
        }

        // 3. Validate job seeker exists via JobSeekerServiceClient
        if (!jobSeekerServiceClient.validateJobSeekerExists(request.getJobSeekerId())) {
            log.warn("Job seeker validation failed: job seeker not found with id {}", request.getJobSeekerId());
            throw new JobSeekerNotFoundException(request.getJobSeekerId());
        }

        // 4. Save and return ApplicationResponse
        Application application = Application.builder()
                .jobId(request.getJobId())
                .jobSeekerId(request.getJobSeekerId())
                .employerId(request.getEmployerId())
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.PENDING)
                .build();

        Application savedApplication = applicationRepository.save(application);
        log.info("Application created successfully with ID: {}", savedApplication.getId());
        return mapToResponse(savedApplication);
    }

    @Override
    public List<ApplicationResponse> getApplicationsByJob(Long jobId) {
        log.info("Fetching applications for jobId: {}", jobId);
        return applicationRepository.findByJobId(jobId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsBySeeker(Long jobSeekerId) {
        log.info("Fetching applications for jobSeekerId: {}", jobSeekerId);
        return applicationRepository.findByJobSeekerId(jobSeekerId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsByEmployer(Long employerId) {
        log.info("Fetching applications for employerId: {}", employerId);
        return applicationRepository.findByEmployerId(employerId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Long id, ApplicationStatus status) {
        log.info("Updating application ID {} status to {}", id, status);
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));

        application.setStatus(status);
        Application updated = applicationRepository.save(application);
        log.info("Application ID {} status updated to {}", id, status);
        return mapToResponse(updated);
    }

    @Override
    public ApplicationResponse getApplicationById(Long id) {
        log.info("Fetching application by ID: {}", id);
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
        return mapToResponse(application);
    }

    private ApplicationResponse mapToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJobId())
                .jobSeekerId(application.getJobSeekerId())
                .employerId(application.getEmployerId())
                .coverLetter(application.getCoverLetter())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}
