package com.jobsearch.applicationservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {

    @NotNull(message = "Job ID is required")
    private Long jobId;

    @NotNull(message = "Job seeker ID is required")
    private Long jobSeekerId;

    @NotNull(message = "Employer ID is required")
    private Long employerId;

    private String coverLetter;
}
