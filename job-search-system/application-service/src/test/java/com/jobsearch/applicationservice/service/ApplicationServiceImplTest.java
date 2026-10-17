package com.jobsearch.applicationservice.service;

import com.jobsearch.applicationservice.client.JobSeekerServiceClient;
import com.jobsearch.applicationservice.client.JobServiceClient;
import com.jobsearch.applicationservice.dto.ApplicationRequest;
import com.jobsearch.applicationservice.dto.ApplicationResponse;
import com.jobsearch.applicationservice.entity.Application;
import com.jobsearch.applicationservice.enums.ApplicationStatus;
import com.jobsearch.applicationservice.exception.ApplicationNotFoundException;
import com.jobsearch.applicationservice.exception.DuplicateApplicationException;
import com.jobsearch.applicationservice.exception.JobNotFoundException;
import com.jobsearch.applicationservice.exception.JobSeekerNotFoundException;
import com.jobsearch.applicationservice.repository.ApplicationRepository;
import com.jobsearch.applicationservice.service.impl.ApplicationServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobServiceClient jobServiceClient;

    @Mock
    private JobSeekerServiceClient jobSeekerServiceClient;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private Application sampleApplication;
    private ApplicationRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleApplication = Application.builder()
                .id(1L)
                .jobId(100L)
                .jobSeekerId(200L)
                .employerId(300L)
                .coverLetter("I am an experienced Java developer interested in this role.")
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = ApplicationRequest.builder()
                .jobId(100L)
                .jobSeekerId(200L)
                .employerId(300L)
                .coverLetter("I am an experienced Java developer interested in this role.")
                .build();
    }

    @Test
    @DisplayName("Should successfully apply for a job when validations pass")
    void applyForJob_Success() {
        when(applicationRepository.existsByJobIdAndJobSeekerId(100L, 200L)).thenReturn(false);
        when(jobServiceClient.validateJobExists(100L)).thenReturn(true);
        when(jobSeekerServiceClient.validateJobSeekerExists(200L)).thenReturn(true);
        when(applicationRepository.save(any(Application.class))).thenReturn(sampleApplication);

        ApplicationResponse response = applicationService.applyForJob(sampleRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getJobId()).isEqualTo(100L);
        assertThat(response.getJobSeekerId()).isEqualTo(200L);
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.PENDING);

        verify(applicationRepository, times(1)).existsByJobIdAndJobSeekerId(100L, 200L);
        verify(jobServiceClient, times(1)).validateJobExists(100L);
        verify(jobSeekerServiceClient, times(1)).validateJobSeekerExists(200L);
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    @DisplayName("Should throw DuplicateApplicationException when job seeker already applied")
    void applyForJob_Duplicate_ThrowsDuplicateApplicationException() {
        when(applicationRepository.existsByJobIdAndJobSeekerId(100L, 200L)).thenReturn(true);

        assertThatThrownBy(() -> applicationService.applyForJob(sampleRequest))
                .isInstanceOf(DuplicateApplicationException.class)
                .hasMessageContaining("already applied");

        verify(applicationRepository, times(1)).existsByJobIdAndJobSeekerId(100L, 200L);
        verify(jobServiceClient, never()).validateJobExists(any());
        verify(jobSeekerServiceClient, never()).validateJobSeekerExists(any());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw JobNotFoundException when job does not exist in job service")
    void applyForJob_JobNotFound_ThrowsJobNotFoundException() {
        when(applicationRepository.existsByJobIdAndJobSeekerId(100L, 200L)).thenReturn(false);
        when(jobServiceClient.validateJobExists(100L)).thenReturn(false);

        assertThatThrownBy(() -> applicationService.applyForJob(sampleRequest))
                .isInstanceOf(JobNotFoundException.class)
                .hasMessageContaining("100");

        verify(applicationRepository, times(1)).existsByJobIdAndJobSeekerId(100L, 200L);
        verify(jobServiceClient, times(1)).validateJobExists(100L);
        verify(jobSeekerServiceClient, never()).validateJobSeekerExists(any());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw JobSeekerNotFoundException when job seeker does not exist in job seeker service")
    void applyForJob_JobSeekerNotFound_ThrowsJobSeekerNotFoundException() {
        when(applicationRepository.existsByJobIdAndJobSeekerId(100L, 200L)).thenReturn(false);
        when(jobServiceClient.validateJobExists(100L)).thenReturn(true);
        when(jobSeekerServiceClient.validateJobSeekerExists(200L)).thenReturn(false);

        assertThatThrownBy(() -> applicationService.applyForJob(sampleRequest))
                .isInstanceOf(JobSeekerNotFoundException.class)
                .hasMessageContaining("200");

        verify(applicationRepository, times(1)).existsByJobIdAndJobSeekerId(100L, 200L);
        verify(jobServiceClient, times(1)).validateJobExists(100L);
        verify(jobSeekerServiceClient, times(1)).validateJobSeekerExists(200L);
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return list of applications for a specific job")
    void getApplicationsByJob_ReturnsList() {
        when(applicationRepository.findByJobId(100L)).thenReturn(List.of(sampleApplication));

        List<ApplicationResponse> results = applicationService.getApplicationsByJob(100L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getJobId()).isEqualTo(100L);
        verify(applicationRepository, times(1)).findByJobId(100L);
    }

    @Test
    @DisplayName("Should return list of applications for a specific job seeker")
    void getApplicationsBySeeker_ReturnsList() {
        when(applicationRepository.findByJobSeekerId(200L)).thenReturn(List.of(sampleApplication));

        List<ApplicationResponse> results = applicationService.getApplicationsBySeeker(200L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getJobSeekerId()).isEqualTo(200L);
        verify(applicationRepository, times(1)).findByJobSeekerId(200L);
    }

    @Test
    @DisplayName("Should return list of applications for a specific employer")
    void getApplicationsByEmployer_ReturnsList() {
        when(applicationRepository.findByEmployerId(300L)).thenReturn(List.of(sampleApplication));

        List<ApplicationResponse> results = applicationService.getApplicationsByEmployer(300L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmployerId()).isEqualTo(300L);
        verify(applicationRepository, times(1)).findByEmployerId(300L);
    }

    @Test
    @DisplayName("Should return application response when application exists by ID")
    void getApplicationById_Success() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(sampleApplication));

        ApplicationResponse response = applicationService.getApplicationById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        verify(applicationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ApplicationNotFoundException when application does not exist by ID")
    void getApplicationById_NotFound_ThrowsApplicationNotFoundException() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getApplicationById(999L))
                .isInstanceOf(ApplicationNotFoundException.class)
                .hasMessageContaining("999");

        verify(applicationRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should successfully update application status to SHORTLISTED")
    void updateApplicationStatus_Success() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(sampleApplication));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationResponse response = applicationService.updateApplicationStatus(1L, ApplicationStatus.SHORTLISTED);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.SHORTLISTED);

        verify(applicationRepository, times(1)).findById(1L);
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    @DisplayName("Should throw ApplicationNotFoundException when updating status for non-existent application")
    void updateApplicationStatus_NotFound_ThrowsApplicationNotFoundException() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.updateApplicationStatus(999L, ApplicationStatus.SHORTLISTED))
                .isInstanceOf(ApplicationNotFoundException.class)
                .hasMessageContaining("999");

        verify(applicationRepository, times(1)).findById(999L);
        verify(applicationRepository, never()).save(any());
    }
}
