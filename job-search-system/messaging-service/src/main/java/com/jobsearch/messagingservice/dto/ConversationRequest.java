package com.jobsearch.messagingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request criteria to retrieve a conversation between an employer and a job seeker for a specific job")
public class ConversationRequest {

    @NotNull(message = "Job ID is required")
    @Schema(description = "ID of the job opening", example = "500")
    private Long jobId;

    @NotNull(message = "Job seeker ID is required")
    @Schema(description = "ID of the job seeker", example = "101")
    private Long jobSeekerId;

    @NotNull(message = "Employer ID is required")
    @Schema(description = "ID of the employer", example = "10")
    private Long employerId;
}
