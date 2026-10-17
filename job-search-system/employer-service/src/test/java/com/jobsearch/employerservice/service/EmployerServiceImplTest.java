package com.jobsearch.employerservice.service;

import com.jobsearch.employerservice.dto.EmployerRequest;
import com.jobsearch.employerservice.dto.EmployerResponse;
import com.jobsearch.employerservice.entity.Employer;
import com.jobsearch.employerservice.exception.DuplicateEmployerException;
import com.jobsearch.employerservice.exception.EmployerNotFoundException;
import com.jobsearch.employerservice.repository.EmployerRepository;
import com.jobsearch.employerservice.service.impl.EmployerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployerServiceImplTest {

    @Mock
    private EmployerRepository employerRepository;

    @InjectMocks
    private EmployerServiceImpl employerService;

    private Employer employer;
    private EmployerRequest employerRequest;

    @BeforeEach
    void setUp() {
        employer = Employer.builder()
                .id(1L)
                .userId(101L)
                .organizationName("Acme Corp")
                .email("hr@acme.com")
                .contactNumber("9876543210")
                .location("San Francisco, CA")
                .about("Innovative tech company")
                .website("https://acme.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        employerRequest = EmployerRequest.builder()
                .userId(101L)
                .organizationName("Acme Corp")
                .email("hr@acme.com")
                .contactNumber("9876543210")
                .location("San Francisco, CA")
                .about("Innovative tech company")
                .website("https://acme.com")
                .build();
    }

    @Test
    @DisplayName("Should successfully create an employer profile")
    void createEmployer_Success() {
        when(employerRepository.existsByEmail(employerRequest.getEmail())).thenReturn(false);
        when(employerRepository.existsByUserId(employerRequest.getUserId())).thenReturn(false);
        when(employerRepository.save(any(Employer.class))).thenReturn(employer);

        EmployerResponse response = employerService.createEmployer(employerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(101L);
        assertThat(response.getOrganizationName()).isEqualTo("Acme Corp");
        assertThat(response.getEmail()).isEqualTo("hr@acme.com");
        verify(employerRepository, times(1)).save(any(Employer.class));
    }

    @Test
    @DisplayName("Should throw DuplicateEmployerException when creating employer with existing email")
    void createEmployer_DuplicateEmail_ThrowsDuplicateEmployerException() {
        when(employerRepository.existsByEmail(employerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> employerService.createEmployer(employerRequest))
                .isInstanceOf(DuplicateEmployerException.class)
                .hasMessageContaining("already exists");

        verify(employerRepository, never()).save(any(Employer.class));
    }

    @Test
    @DisplayName("Should throw DuplicateEmployerException when creating employer with existing userId")
    void createEmployer_DuplicateUserId_ThrowsDuplicateEmployerException() {
        when(employerRepository.existsByEmail(employerRequest.getEmail())).thenReturn(false);
        when(employerRepository.existsByUserId(employerRequest.getUserId())).thenReturn(true);

        assertThatThrownBy(() -> employerService.createEmployer(employerRequest))
                .isInstanceOf(DuplicateEmployerException.class)
                .hasMessageContaining("Employer profile already exists for user ID");

        verify(employerRepository, never()).save(any(Employer.class));
    }

    @Test
    @DisplayName("Should successfully retrieve an employer by ID")
    void getEmployerById_Success() {
        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));

        EmployerResponse response = employerService.getEmployerById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrganizationName()).isEqualTo("Acme Corp");
    }

    @Test
    @DisplayName("Should throw EmployerNotFoundException when employer is not found by ID")
    void getEmployerById_NotFound_ThrowsEmployerNotFoundException() {
        when(employerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employerService.getEmployerById(99L))
                .isInstanceOf(EmployerNotFoundException.class)
                .hasMessageContaining("Employer not found with id: 99");
    }

    @Test
    @DisplayName("Should successfully retrieve an employer by user ID")
    void getEmployerByUserId_Success() {
        when(employerRepository.findByUserId(101L)).thenReturn(Optional.of(employer));

        EmployerResponse response = employerService.getEmployerByUserId(101L);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should throw EmployerNotFoundException when employer is not found by user ID")
    void getEmployerByUserId_NotFound_ThrowsEmployerNotFoundException() {
        when(employerRepository.findByUserId(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employerService.getEmployerByUserId(999L))
                .isInstanceOf(EmployerNotFoundException.class)
                .hasMessageContaining("Employer not found with userId: 999");
    }

    @Test
    @DisplayName("Should successfully retrieve all employers")
    void getAllEmployers_Success() {
        when(employerRepository.findAll()).thenReturn(List.of(employer));

        List<EmployerResponse> responses = employerService.getAllEmployers();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getOrganizationName()).isEqualTo("Acme Corp");
    }

    @Test
    @DisplayName("Should successfully update an employer profile")
    void updateEmployer_Success() {
        EmployerRequest updateRequest = EmployerRequest.builder()
                .userId(101L)
                .organizationName("Acme Global")
                .email("hr@acmeglobal.com")
                .contactNumber("1122334455")
                .location("New York, NY")
                .about("Global leader in technology")
                .website("https://acmeglobal.com")
                .build();

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));
        when(employerRepository.existsByEmail("hr@acmeglobal.com")).thenReturn(false);
        when(employerRepository.save(any(Employer.class))).thenReturn(employer);

        EmployerResponse response = employerService.updateEmployer(1L, updateRequest);

        assertThat(response).isNotNull();
        verify(employerRepository, times(1)).save(employer);
    }

    @Test
    @DisplayName("Should throw EmployerNotFoundException when updating non-existent employer")
    void updateEmployer_NotFound_ThrowsEmployerNotFoundException() {
        when(employerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employerService.updateEmployer(99L, employerRequest))
                .isInstanceOf(EmployerNotFoundException.class)
                .hasMessageContaining("Employer not found with id: 99");

        verify(employerRepository, never()).save(any(Employer.class));
    }
}
