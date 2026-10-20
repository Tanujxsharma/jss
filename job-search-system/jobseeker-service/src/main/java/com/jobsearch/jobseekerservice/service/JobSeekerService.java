package com.jobsearch.jobseekerservice.service;

import com.jobsearch.jobseekerservice.dto.JobSeekerRequest;
import com.jobsearch.jobseekerservice.dto.JobSeekerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface JobSeekerService {

    JobSeekerResponse createJobSeeker(JobSeekerRequest request, Long authenticatedUserId);

    JobSeekerResponse getJobSeekerById(Long id);

    JobSeekerResponse getJobSeekerByUserId(Long userId);

    JobSeekerResponse updateJobSeeker(Long id, JobSeekerRequest request, Long authenticatedUserId);

    JobSeekerResponse uploadResume(Long id, MultipartFile file, Long authenticatedUserId);

    List<JobSeekerResponse> searchCandidates(String skills, String location, BigDecimal minExperience);
}
