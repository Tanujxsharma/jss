package com.jobsearch.applicationservice.service.impl;

import com.jobsearch.applicationservice.client.JobSeekerServiceClient;
import com.jobsearch.applicationservice.client.JobServiceClient;
import com.jobsearch.applicationservice.client.JobServiceResponse;
import com.jobsearch.applicationservice.client.JobSeekerServiceResponse;
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
import org.springframework.security.access.AccessDeniedException;
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
    public ApplicationResponse applyForJob(ApplicationRequest request, String authorization, Long authenticatedUserId) {
        log.info("Processing application for jobId: {} by jobSeekerId: {}",
                request.getJobId(), request.getJobSeekerId());

        // 1. Check duplicate application
        if (applicationRepository.existsByJobIdAndJobSeekerId(request.getJobId(), request.getJobSeekerId())) {
            log.warn("Duplicate application detected for jobId: {} and jobSeekerId: {}",
                    request.getJobId(), request.getJobSeekerId());
            throw new DuplicateApplicationException(request.getJobId(), request.getJobSeekerId());
        }

        // 2. Verify the job and its owning employer through Job Service.
        JobServiceResponse job = jobServiceClient.getJob(request.getJobId(), authorization)
                .orElseThrow(() -> new JobNotFoundException(request.getJobId()));
        if (!request.getEmployerId().equals(job.getEmployerId())) {
            throw new IllegalArgumentException("The employer ID does not match the selected job");
        }

        // 3. Verify that the applicant profile belongs to the authenticated user.
        JobSeekerServiceResponse jobSeeker = jobSeekerServiceClient
                .getJobSeeker(request.getJobSeekerId(), authorization)
                .orElseThrow(() -> new JobSeekerNotFoundException(request.getJobSeekerId()));
        if (!authenticatedUserId.equals(jobSeeker.getUserId())) {
            throw new AccessDeniedException("Cannot apply using another job seeker's profile");
        }

        // 4. Save and return ApplicationResponse
        Application application = Application.builder()
                .jobId(request.getJobId())
                .jobSeekerId(request.getJobSeekerId())
                .jobSeekerUserId(authenticatedUserId)
                .employerId(request.getEmployerId())
                .employerUserId(job.getEmployerUserId())
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.PENDING)
                .build();

        Application savedApplication = applicationRepository.save(application);
        log.info("Application created successfully with ID: {}", savedApplication.getId());
        return mapToResponse(savedApplication);
    }

    @Override
    public List<ApplicationResponse> getApplicationsByJob(Long jobId, Long authenticatedUserId) {
        log.info("Fetching applications for jobId: {}", jobId);
        return applicationRepository.findByJobIdAndEmployerUserId(jobId, authenticatedUserId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsBySeeker(Long jobSeekerId, Long authenticatedUserId) {
        log.info("Fetching applications for jobSeekerId: {}", jobSeekerId);
        return applicationRepository.findByJobSeekerUserId(authenticatedUserId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsByEmployer(Long employerId, Long authenticatedUserId) {
        log.info("Fetching applications for employerId: {}", employerId);
        return applicationRepository.findByEmployerIdAndEmployerUserId(employerId, authenticatedUserId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Long id, ApplicationStatus status, Long authenticatedUserId) {
        log.info("Updating application ID {} status to {}", id, status);
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
        if (application.getEmployerUserId() == null
                || !application.getEmployerUserId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("Cannot change an application for another employer's job");
        }

        application.setStatus(status);
        Application updated = applicationRepository.save(application);
        log.info("Application ID {} status updated to {}", id, status);
        return mapToResponse(updated);
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
