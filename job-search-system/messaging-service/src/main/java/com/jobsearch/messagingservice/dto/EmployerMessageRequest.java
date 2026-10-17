package com.jobsearch.messagingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for an employer sending a message to a job seeker")
public class EmployerMessageRequest {

    @NotNull(message = "Employer ID is required")
    @Schema(description = "ID of the employer sending the message", example = "10")
    private Long employerId;

    @NotNull(message = "Job seeker ID is required")
    @Schema(description = "ID of the recipient job seeker", example = "101")
    private Long jobSeekerId;

    @NotNull(message = "Job ID is required")
    @Schema(description = "ID of the job opening related to this message", example = "500")
    private Long jobId;

    @NotBlank(message = "Subject is required")
    @Schema(description = "Subject line of the message", example = "Interview Invitation - Senior Java Developer")
    private String subject;

    @NotBlank(message = "Message description is required")
    @Schema(description = "Body content of the message", example = "We were impressed with your application and would like to schedule an interview.")
    private String messageDescription;
}
