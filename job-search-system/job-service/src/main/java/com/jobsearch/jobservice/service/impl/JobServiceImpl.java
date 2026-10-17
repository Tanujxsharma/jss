package com.jobsearch.jobservice.service.impl;

import com.jobsearch.jobservice.dto.JobRequest;
import com.jobsearch.jobservice.dto.JobResponse;
import com.jobsearch.jobservice.entity.Job;
import com.jobsearch.jobservice.enums.JobStatus;
import com.jobsearch.jobservice.exception.JobNotFoundException;
import com.jobsearch.jobservice.repository.JobRepository;
import com.jobsearch.jobservice.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Override
    @Transactional
    public JobResponse createJob(JobRequest request) {
        log.info("Creating new job with title: '{}' for employerId: {}", request.getTitle(), request.getEmployerId());

        Job job = Job.builder()
                .employerId(request.getEmployerId())
                .title(request.getTitle())
                .companyName(request.getCompanyName())
                .location(request.getLocation())
                .description(request.getDescription())
                .skillsRequired(request.getSkillsRequired())
                .experienceRequired(request.getExperienceRequired())
                .salaryRange(request.getSalaryRange())
                .jobType(request.getJobType())
                .status(JobStatus.ACTIVE)
                .build();

        Job savedJob = jobRepository.save(job);
        log.info("Successfully created job with ID: {}", savedJob.getId());
        return JobResponse.fromEntity(savedJob);
    }

    @Override
    public JobResponse getJobById(Long id) {
        log.info("Fetching job with ID: {}", id);
        return jobRepository.findById(id)
                .map(JobResponse::fromEntity)
                .orElseThrow(() -> new JobNotFoundException(id));
    }

    @Override
    public List<JobResponse> getAllActiveJobs() {
        log.info("Fetching all active jobs");
        return jobRepository.findByStatus(JobStatus.ACTIVE).stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    @Override
    public List<JobResponse> searchJobs(String title, String location, String skills, String company) {
        String trimmedTitle = (title != null && !title.isBlank()) ? title.trim() : null;
        String trimmedLocation = (location != null && !location.isBlank()) ? location.trim() : null;
        String trimmedSkills = (skills != null && !skills.isBlank()) ? skills.trim() : null;
        String trimmedCompany = (company != null && !company.isBlank()) ? company.trim() : null;

        log.info("Searching active jobs with title: '{}', location: '{}', skills: '{}', company: '{}'",
                trimmedTitle, trimmedLocation, trimmedSkills, trimmedCompany);

        return jobRepository.searchJobs(trimmedTitle, trimmedLocation, trimmedSkills, trimmedCompany).stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    @Override
    public List<JobResponse> getJobsByEmployer(Long employerId) {
        log.info("Fetching jobs for employerId: {}", employerId);
        return jobRepository.findByEmployerId(employerId).stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long id, JobRequest request) {
        log.info("Updating job with ID: {}", id);

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));

        if (request.getEmployerId() != null) {
            job.setEmployerId(request.getEmployerId());
        }
        job.setTitle(request.getTitle());
        job.setCompanyName(request.getCompanyName());
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setSkillsRequired(request.getSkillsRequired());
        job.setExperienceRequired(request.getExperienceRequired());
        job.setSalaryRange(request.getSalaryRange());
        job.setJobType(request.getJobType());

        Job updatedJob = jobRepository.save(job);
        log.info("Successfully updated job with ID: {}", updatedJob.getId());
        return JobResponse.fromEntity(updatedJob);
    }

    @Override
    @Transactional
    public void deleteJob(Long id) {
        log.info("Deleting job with ID: {}", id);
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
        jobRepository.delete(job);
        log.info("Successfully deleted job with ID: {}", id);
    }

    @Override
    @Transactional
    public JobResponse closeJob(Long id) {
        log.info("Closing job with ID: {}", id);
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));
        job.setStatus(JobStatus.CLOSED);
        Job closedJob = jobRepository.save(job);
        log.info("Successfully closed job with ID: {}", closedJob.getId());
        return JobResponse.fromEntity(closedJob);
    }
}
