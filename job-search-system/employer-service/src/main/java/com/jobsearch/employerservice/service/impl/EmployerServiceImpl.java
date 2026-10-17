package com.jobsearch.employerservice.service.impl;

import com.jobsearch.employerservice.dto.EmployerRequest;
import com.jobsearch.employerservice.dto.EmployerResponse;
import com.jobsearch.employerservice.entity.Employer;
import com.jobsearch.employerservice.exception.DuplicateEmployerException;
import com.jobsearch.employerservice.exception.EmployerNotFoundException;
import com.jobsearch.employerservice.repository.EmployerRepository;
import com.jobsearch.employerservice.service.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;

    @Override
    @Transactional
    public EmployerResponse createEmployer(EmployerRequest request) {
        if (employerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmployerException("Employer with email " + request.getEmail() + " already exists");
        }

        if (employerRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateEmployerException("Employer profile already exists for user ID: " + request.getUserId());
        }

        Employer employer = Employer.builder()
                .userId(request.getUserId())
                .organizationName(request.getOrganizationName())
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .location(request.getLocation())
                .about(request.getAbout())
                .website(request.getWebsite())
                .build();

        Employer savedEmployer = employerRepository.save(employer);
        return mapToResponse(savedEmployer);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployerResponse getEmployerById(Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new EmployerNotFoundException("Employer not found with id: " + id));
        return mapToResponse(employer);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployerResponse getEmployerByUserId(Long userId) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new EmployerNotFoundException("Employer not found with userId: " + userId));
        return mapToResponse(employer);
    }

    @Override
    @Transactional
    public EmployerResponse updateEmployer(Long id, EmployerRequest request) {
        Employer existingEmployer = employerRepository.findById(id)
                .orElseThrow(() -> new EmployerNotFoundException("Employer not found with id: " + id));

        if (!existingEmployer.getEmail().equalsIgnoreCase(request.getEmail())
                && employerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmployerException("Employer with email " + request.getEmail() + " already exists");
        }

        if (request.getUserId() != null && !existingEmployer.getUserId().equals(request.getUserId())
                && employerRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateEmployerException("Employer profile already exists for user ID: " + request.getUserId());
        }

        existingEmployer.setOrganizationName(request.getOrganizationName());
        existingEmployer.setEmail(request.getEmail());
        existingEmployer.setContactNumber(request.getContactNumber());
        existingEmployer.setLocation(request.getLocation());
        existingEmployer.setAbout(request.getAbout());
        existingEmployer.setWebsite(request.getWebsite());
        if (request.getUserId() != null) {
            existingEmployer.setUserId(request.getUserId());
        }

        Employer updatedEmployer = employerRepository.save(existingEmployer);
        return mapToResponse(updatedEmployer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployerResponse> getAllEmployers() {
        return employerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private EmployerResponse mapToResponse(Employer employer) {
        return EmployerResponse.builder()
                .id(employer.getId())
                .userId(employer.getUserId())
                .organizationName(employer.getOrganizationName())
                .email(employer.getEmail())
                .contactNumber(employer.getContactNumber())
                .location(employer.getLocation())
                .about(employer.getAbout())
                .website(employer.getWebsite())
                .createdAt(employer.getCreatedAt())
                .updatedAt(employer.getUpdatedAt())
                .build();
    }
}
