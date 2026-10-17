package com.jobsearch.applicationservice.service;

import com.jobsearch.applicationservice.dto.ApplicationRequest;
import com.jobsearch.applicationservice.dto.ApplicationResponse;
import com.jobsearch.applicationservice.enums.ApplicationStatus;

import java.util.List;

public interface ApplicationService {

    ApplicationResponse applyForJob(ApplicationRequest request);

    List<ApplicationResponse> getApplicationsByJob(Long jobId);

    List<ApplicationResponse> getApplicationsBySeeker(Long jobSeekerId);

    List<ApplicationResponse> getApplicationsByEmployer(Long employerId);

    ApplicationResponse updateApplicationStatus(Long id, ApplicationStatus status);

    ApplicationResponse getApplicationById(Long id);
}
