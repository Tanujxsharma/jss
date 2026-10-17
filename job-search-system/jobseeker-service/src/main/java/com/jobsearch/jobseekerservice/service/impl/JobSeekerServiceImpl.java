package com.jobsearch.jobseekerservice.service.impl;

import com.jobsearch.jobseekerservice.config.FileStorageConfig;
import com.jobsearch.jobseekerservice.dto.JobSeekerRequest;
import com.jobsearch.jobseekerservice.dto.JobSeekerResponse;
import com.jobsearch.jobseekerservice.entity.JobSeeker;
import com.jobsearch.jobseekerservice.exception.DuplicateJobSeekerException;
import com.jobsearch.jobseekerservice.exception.FileStorageException;
import com.jobsearch.jobseekerservice.exception.JobSeekerNotFoundException;
import com.jobsearch.jobseekerservice.repository.JobSeekerRepository;
import com.jobsearch.jobseekerservice.service.JobSeekerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JobSeekerServiceImpl implements JobSeekerService {

    private final JobSeekerRepository jobSeekerRepository;
    private final FileStorageConfig fileStorageConfig;

    @Override
    @Transactional
    public JobSeekerResponse createJobSeeker(JobSeekerRequest request) {
        log.info("Creating job seeker for userId: {}, email: {}", request.getUserId(), request.getEmail());

        if (jobSeekerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateJobSeekerException("JobSeeker with email " + request.getEmail() + " already exists");
        }

        if (jobSeekerRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new DuplicateJobSeekerException("JobSeeker with userId " + request.getUserId() + " already exists");
        }

        JobSeeker jobSeeker = JobSeeker.builder()
                .userId(request.getUserId())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .location(request.getLocation())
                .skills(request.getSkills())
                .experienceYears(request.getExperienceYears())
                .summary(request.getSummary())
                .build();

        JobSeeker saved = jobSeekerRepository.save(jobSeeker);
        log.info("Successfully created job seeker with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    public JobSeekerResponse getJobSeekerById(Long id) {
        log.info("Fetching job seeker with id: {}", id);
        JobSeeker jobSeeker = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new JobSeekerNotFoundException("JobSeeker not found with id: " + id));
        return mapToResponse(jobSeeker);
    }

    @Override
    public JobSeekerResponse getJobSeekerByUserId(Long userId) {
        log.info("Fetching job seeker with userId: {}", userId);
        JobSeeker jobSeeker = jobSeekerRepository.findByUserId(userId)
                .orElseThrow(() -> new JobSeekerNotFoundException("JobSeeker not found with userId: " + userId));
        return mapToResponse(jobSeeker);
    }

    @Override
    @Transactional
    public JobSeekerResponse updateJobSeeker(Long id, JobSeekerRequest request) {
        log.info("Updating job seeker with id: {}", id);
        JobSeeker existing = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new JobSeekerNotFoundException("JobSeeker not found with id: " + id));

        if (!existing.getEmail().equalsIgnoreCase(request.getEmail()) &&
                jobSeekerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateJobSeekerException("Email " + request.getEmail() + " is already in use");
        }

        existing.setFullName(request.getFullName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setLocation(request.getLocation());
        existing.setSkills(request.getSkills());
        existing.setExperienceYears(request.getExperienceYears());
        existing.setSummary(request.getSummary());

        JobSeeker updated = jobSeekerRepository.save(existing);
        log.info("Successfully updated job seeker with id: {}", updated.getId());
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public JobSeekerResponse uploadResume(Long id, MultipartFile file) {
        log.info("Uploading resume for job seeker id: {}", id);

        JobSeeker jobSeeker = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new JobSeekerNotFoundException("JobSeeker not found with id: " + id));

        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Cannot upload empty file");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "resume";
        }
        originalFilename = StringUtils.cleanPath(originalFilename);

        if (originalFilename.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence: " + originalFilename);
        }

        try {
            Path targetDir = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            String fileName = id + "_" + originalFilename;
            Path targetLocation = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String resumeUrl = "/uploads/resumes/" + fileName;
            jobSeeker.setResumeUrl(resumeUrl);

            JobSeeker saved = jobSeekerRepository.save(jobSeeker);
            log.info("Successfully uploaded resume for job seeker id: {} to {}", id, resumeUrl);
            return mapToResponse(saved);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFilename, ex);
        }
    }

    @Override
    public List<JobSeekerResponse> searchCandidates(String skills, String location, BigDecimal minExperience) {
        log.info("Searching candidates with skills: {}, location: {}, minExperience: {}", skills, location, minExperience);
        List<JobSeeker> candidates = jobSeekerRepository.searchCandidates(skills, location, minExperience);
        return candidates.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private JobSeekerResponse mapToResponse(JobSeeker jobSeeker) {
        return JobSeekerResponse.builder()
                .id(jobSeeker.getId())
                .userId(jobSeeker.getUserId())
                .fullName(jobSeeker.getFullName())
                .email(jobSeeker.getEmail())
                .phone(jobSeeker.getPhone())
                .location(jobSeeker.getLocation())
                .skills(jobSeeker.getSkills())
                .experienceYears(jobSeeker.getExperienceYears())
                .resumeUrl(jobSeeker.getResumeUrl())
                .summary(jobSeeker.getSummary())
                .createdAt(jobSeeker.getCreatedAt())
                .updatedAt(jobSeeker.getUpdatedAt())
                .build();
    }
}
