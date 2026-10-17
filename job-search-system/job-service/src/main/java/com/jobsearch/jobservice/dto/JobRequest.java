package com.jobsearch.jobservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a job posting")
public class JobRequest {

    @NotNull(message = "Employer ID is required")
    @Schema(description = "ID of the employer posting the job", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long employerId;

    @NotBlank(message = "Job title is required")
    @Schema(description = "Title of the job posting", example = "Senior Java Developer", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Company name", example = "Tech Corp")
    private String companyName;

    @Schema(description = "Job location or Remote", example = "Bangalore, India")
    private String location;

    @Schema(description = "Detailed job description", example = "Looking for an experienced Spring Boot engineer...")
    private String description;

    @NotBlank(message = "Skills required is required")
    @Schema(description = "Required skills (comma-separated or free text)", example = "Java, Spring Boot, Microservices, Docker", requiredMode = Schema.RequiredMode.REQUIRED)
    private String skillsRequired;

    @DecimalMin(value = "0.0", inclusive = true, message = "Experience required must be greater than or equal to 0.0")
    @Schema(description = "Years of experience required", example = "3.5")
    private BigDecimal experienceRequired;

    @Schema(description = "Salary range", example = "$90,000 - $120,000")
    private String salaryRange;

    @Schema(description = "Type of employment (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP)", example = "FULL_TIME")
    private String jobType;
}
