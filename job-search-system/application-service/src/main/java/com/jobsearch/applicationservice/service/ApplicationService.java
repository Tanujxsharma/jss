package com.jobsearch.applicationservice.service;

import com.jobsearch.applicationservice.dto.ApplicationRequest;
import com.jobsearch.applicationservice.dto.ApplicationResponse;
import com.jobsearch.applicationservice.enums.ApplicationStatus;

import java.util.List;

public interface ApplicationService {

    ApplicationResponse applyForJob(ApplicationRequest request, String authorization, Long authenticatedUserId);

    List<ApplicationResponse> getApplicationsByJob(Long jobId, Long authenticatedUserId);

    List<ApplicationResponse> getApplicationsBySeeker(Long jobSeekerId, Long authenticatedUserId);

    List<ApplicationResponse> getApplicationsByEmployer(Long employerId, Long authenticatedUserId);

    ApplicationResponse updateApplicationStatus(Long id, ApplicationStatus status, Long authenticatedUserId);

}
