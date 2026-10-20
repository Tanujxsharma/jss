package com.jobsearch.jobservice.service;

import com.jobsearch.jobservice.dto.JobRequest;
import com.jobsearch.jobservice.dto.JobResponse;

import java.util.List;

public interface JobService {

    JobResponse createJob(JobRequest request, Long authenticatedUserId, String authorization);

    JobResponse getJobById(Long id);

    List<JobResponse> getAllActiveJobs();

    List<JobResponse> searchJobs(String title, String location, String skills, String company);

    List<JobResponse> getJobsByEmployer(Long employerId);

    JobResponse updateJob(Long id, JobRequest request, Long authenticatedUserId);

    void deleteJob(Long id, Long authenticatedUserId);

    JobResponse closeJob(Long id, Long authenticatedUserId);
}
