package com.jobsearch.jobservice.service;

import com.jobsearch.jobservice.dto.JobRequest;
import com.jobsearch.jobservice.dto.JobResponse;
import com.jobsearch.jobservice.entity.Job;
import com.jobsearch.jobservice.enums.JobStatus;
import com.jobsearch.jobservice.exception.JobNotFoundException;
import com.jobsearch.jobservice.repository.JobRepository;
import com.jobsearch.jobservice.service.impl.JobServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    private Job sampleJob;
    private JobRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleJob = Job.builder()
                .id(1L)
                .employerId(10L)
                .title("Software Engineer")
                .companyName("Acme Corp")
                .location("New York, NY")
                .description("Build scalable backend services")
                .skillsRequired("Java, Spring Boot, MySQL")
                .experienceRequired(new BigDecimal("3.0"))
                .salaryRange("$100,000 - $130,000")
                .jobType("FULL_TIME")
                .status(JobStatus.ACTIVE)
                .postedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = JobRequest.builder()
                .employerId(10L)
                .title("Software Engineer")
                .companyName("Acme Corp")
                .location("New York, NY")
                .description("Build scalable backend services")
                .skillsRequired("Java, Spring Boot, MySQL")
                .experienceRequired(new BigDecimal("3.0"))
                .salaryRange("$100,000 - $130,000")
                .jobType("FULL_TIME")
                .build();
    }

    @Test
    @DisplayName("Should successfully create a job and return JobResponse")
    void createJob_Success() {
        when(jobRepository.save(any(Job.class))).thenReturn(sampleJob);

        JobResponse response = jobService.createJob(sampleRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Software Engineer");
        assertThat(response.getStatus()).isEqualTo(JobStatus.ACTIVE);
        assertThat(response.getEmployerId()).isEqualTo(10L);

        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    @DisplayName("Should return JobResponse when job is found by ID")
    void getJobById_Success() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(sampleJob));

        JobResponse response = jobService.getJobById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Software Engineer");

        verify(jobRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw JobNotFoundException when job is not found by ID")
    void getJobById_NotFound_ThrowsJobNotFoundException() {
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.getJobById(999L))
                .isInstanceOf(JobNotFoundException.class)
                .hasMessageContaining("999");

        verify(jobRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should return filtered list of active jobs when searching")
    void searchJobs_ReturnsFilteredList() {
        when(jobRepository.searchJobs("Engineer", "New York", "Java", "Acme"))
                .thenReturn(List.of(sampleJob));

        List<JobResponse> results = jobService.searchJobs("Engineer", "New York", "Java", "Acme");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Software Engineer");
        assertThat(results.get(0).getCompanyName()).isEqualTo("Acme Corp");

        verify(jobRepository, times(1)).searchJobs("Engineer", "New York", "Java", "Acme");
    }

    @Test
    @DisplayName("Should return all active jobs")
    void getAllActiveJobs_Success() {
        when(jobRepository.findByStatus(JobStatus.ACTIVE)).thenReturn(List.of(sampleJob));

        List<JobResponse> results = jobService.getAllActiveJobs();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(JobStatus.ACTIVE);

        verify(jobRepository, times(1)).findByStatus(JobStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should return jobs posted by specific employer")
    void getJobsByEmployer_Success() {
        when(jobRepository.findByEmployerId(10L)).thenReturn(List.of(sampleJob));

        List<JobResponse> results = jobService.getJobsByEmployer(10L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmployerId()).isEqualTo(10L);

        verify(jobRepository, times(1)).findByEmployerId(10L);
    }

    @Test
    @DisplayName("Should update an existing job successfully")
    void updateJob_Success() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        when(jobRepository.save(any(Job.class))).thenReturn(sampleJob);

        JobRequest updateRequest = JobRequest.builder()
                .employerId(10L)
                .title("Lead Engineer")
                .companyName("Acme Corp")
                .location("Remote")
                .description("Lead platform engineering")
                .skillsRequired("Java, Kubernetes, Kafka")
                .experienceRequired(new BigDecimal("7.0"))
                .salaryRange("$140,000 - $170,000")
                .jobType("FULL_TIME")
                .build();

        JobResponse response = jobService.updateJob(1L, updateRequest);

        assertThat(response).isNotNull();
        verify(jobRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    @DisplayName("Should throw JobNotFoundException when updating non-existent job")
    void updateJob_NotFound_ThrowsJobNotFoundException() {
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.updateJob(999L, sampleRequest))
                .isInstanceOf(JobNotFoundException.class)
                .hasMessageContaining("999");

        verify(jobRepository, times(1)).findById(999L);
        verify(jobRepository, never()).save(any(Job.class));
    }

    @Test
    @DisplayName("Should successfully delete an existing job")
    void deleteJob_Success() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        doNothing().when(jobRepository).delete(sampleJob);

        jobService.deleteJob(1L);

        verify(jobRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).delete(sampleJob);
    }

    @Test
    @DisplayName("Should throw JobNotFoundException when deleting non-existent job")
    void deleteJob_NotFound_ThrowsJobNotFoundException() {
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.deleteJob(999L))
                .isInstanceOf(JobNotFoundException.class)
                .hasMessageContaining("999");

        verify(jobRepository, times(1)).findById(999L);
        verify(jobRepository, never()).delete(any(Job.class));
    }

    @Test
    @DisplayName("Should close an existing job and set status to CLOSED")
    void closeJob_Success() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JobResponse response = jobService.closeJob(1L);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(JobStatus.CLOSED);

        verify(jobRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    @DisplayName("Should throw JobNotFoundException when closing non-existent job")
    void closeJob_NotFound_ThrowsJobNotFoundException() {
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.closeJob(999L))
                .isInstanceOf(JobNotFoundException.class)
                .hasMessageContaining("999");

        verify(jobRepository, times(1)).findById(999L);
        verify(jobRepository, never()).save(any(Job.class));
    }
}
