package com.jobsearch.jobservice.dto;

import com.jobsearch.jobservice.entity.Job;
import com.jobsearch.jobservice.enums.JobStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response representation of a job posting")
public class JobResponse {

    @Schema(description = "Unique identifier of the job", example = "101")
    private Long id;

    @Schema(description = "ID of the employer who posted the job", example = "1")
    private Long employerId;

    @Schema(description = "Job title", example = "Senior Java Developer")
    private String title;

    @Schema(description = "Company name", example = "Tech Corp")
    private String companyName;

    @Schema(description = "Job location or Remote", example = "Bangalore, India")
    private String location;

    @Schema(description = "Job description")
    private String description;

    @Schema(description = "Required skills", example = "Java, Spring Boot, Microservices")
    private String skillsRequired;

    @Schema(description = "Years of experience required", example = "3.5")
    private BigDecimal experienceRequired;

    @Schema(description = "Salary range", example = "$90,000 - $120,000")
    private String salaryRange;

    @Schema(description = "Type of employment", example = "FULL_TIME")
    private String jobType;

    @Schema(description = "Current job status", example = "ACTIVE")
    private JobStatus status;

    @Schema(description = "Date and time the job was posted")
    private LocalDateTime postedAt;

    @Schema(description = "Date and time the job was last updated")
    private LocalDateTime updatedAt;

    public static JobResponse fromEntity(Job job) {
        if (job == null) {
            return null;
        }
        return JobResponse.builder()
                .id(job.getId())
                .employerId(job.getEmployerId())
                .title(job.getTitle())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .description(job.getDescription())
                .skillsRequired(job.getSkillsRequired())
                .experienceRequired(job.getExperienceRequired())
                .salaryRange(job.getSalaryRange())
                .jobType(job.getJobType())
                .status(job.getStatus())
                .postedAt(job.getPostedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
