package com.jobsearch.jobseekerservice.service;

import com.jobsearch.jobseekerservice.config.FileStorageConfig;
import com.jobsearch.jobseekerservice.dto.JobSeekerRequest;
import com.jobsearch.jobseekerservice.dto.JobSeekerResponse;
import com.jobsearch.jobseekerservice.entity.JobSeeker;
import com.jobsearch.jobseekerservice.exception.DuplicateJobSeekerException;
import com.jobsearch.jobseekerservice.exception.JobSeekerNotFoundException;
import com.jobsearch.jobseekerservice.repository.JobSeekerRepository;
import com.jobsearch.jobseekerservice.service.impl.JobSeekerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobSeekerServiceImplTest {

    @Mock
    private JobSeekerRepository jobSeekerRepository;

    @Mock
    private FileStorageConfig fileStorageConfig;

    @InjectMocks
    private JobSeekerServiceImpl jobSeekerService;

    private JobSeeker sampleJobSeeker;
    private JobSeekerRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleJobSeeker = JobSeeker.builder()
                .id(1L)
                .userId(101L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .location("New York")
                .skills("Java, Spring Boot, MySQL")
                .experienceYears(new BigDecimal("5.0"))
                .resumeUrl(null)
                .summary("Experienced Java developer")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = JobSeekerRequest.builder()
                .userId(101L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .location("New York")
                .skills("Java, Spring Boot, MySQL")
                .experienceYears(new BigDecimal("5.0"))
                .summary("Experienced Java developer")
                .build();
    }

    @Test
    @DisplayName("createJobSeeker - should create and return JobSeekerResponse successfully")
    void createJobSeeker_Success() {
        when(jobSeekerRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(jobSeekerRepository.findByUserId(101L)).thenReturn(Optional.empty());
        when(jobSeekerRepository.save(any(JobSeeker.class))).thenReturn(sampleJobSeeker);

        JobSeekerResponse response = jobSeekerService.createJobSeeker(sampleRequest, 101L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getFullName());
        assertEquals("john.doe@example.com", response.getEmail());
        verify(jobSeekerRepository, times(1)).save(any(JobSeeker.class));
    }

    @Test
    @DisplayName("createJobSeeker - should throw DuplicateJobSeekerException when email already exists")
    void createJobSeeker_EmailExists_ThrowsException() {
        when(jobSeekerRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThrows(DuplicateJobSeekerException.class, () -> jobSeekerService.createJobSeeker(sampleRequest, 101L));
        verify(jobSeekerRepository, never()).save(any(JobSeeker.class));
    }

    @Test
    @DisplayName("getJobSeekerById - should throw JobSeekerNotFoundException when seeker not found")
    void getJobSeekerById_NotFound_ThrowsException() {
        when(jobSeekerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(JobSeekerNotFoundException.class, () -> jobSeekerService.getJobSeekerById(999L));
        verify(jobSeekerRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("searchCandidates - should return filtered list of JobSeekerResponse")
    void searchCandidates_ReturnsFilteredList() {
        JobSeeker candidate2 = JobSeeker.builder()
                .id(2L)
                .userId(102L)
                .fullName("Jane Smith")
                .email("jane.smith@example.com")
                .location("New York")
                .skills("Java, Microservices")
                .experienceYears(new BigDecimal("4.0"))
                .build();

        when(jobSeekerRepository.searchCandidates("Java", "New York", new BigDecimal("3.0")))
                .thenReturn(List.of(sampleJobSeeker, candidate2));

        List<JobSeekerResponse> results = jobSeekerService.searchCandidates("Java", "New York", new BigDecimal("3.0"));

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("John Doe", results.get(0).getFullName());
        assertEquals("Jane Smith", results.get(1).getFullName());
        verify(jobSeekerRepository, times(1)).searchCandidates("Java", "New York", new BigDecimal("3.0"));
    }

    @Test
    @DisplayName("uploadResume - should upload file and update resumeUrl successfully")
    void uploadResume_Success() throws IOException {
        String testUploadDir = "target/test-uploads";
        when(fileStorageConfig.getUploadDir()).thenReturn(testUploadDir);
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(sampleJobSeeker));

        JobSeeker updatedEntity = JobSeeker.builder()
                .id(1L)
                .userId(101L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .resumeUrl("/uploads/resumes/1_resume.pdf")
                .build();

        when(jobSeekerRepository.save(any(JobSeeker.class))).thenReturn(updatedEntity);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Sample resume content".getBytes()
        );

        JobSeekerResponse response = jobSeekerService.uploadResume(1L, mockFile, 101L);

        assertNotNull(response);
        assertEquals("/uploads/resumes/1_resume.pdf", response.getResumeUrl());
        verify(jobSeekerRepository, times(1)).save(any(JobSeeker.class));

        // Clean up created test file/dir if needed
        Path testPath = Paths.get(testUploadDir);
        if (Files.exists(testPath)) {
            Files.walk(testPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
