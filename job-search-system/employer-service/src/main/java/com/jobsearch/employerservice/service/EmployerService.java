package com.jobsearch.employerservice.service;

import com.jobsearch.employerservice.dto.EmployerRequest;
import com.jobsearch.employerservice.dto.EmployerResponse;

import java.util.List;

public interface EmployerService {

    EmployerResponse createEmployer(EmployerRequest request);

    EmployerResponse getEmployerById(Long id);

    EmployerResponse getEmployerByUserId(Long userId);

    EmployerResponse updateEmployer(Long id, EmployerRequest request);

    List<EmployerResponse> getAllEmployers();
}
