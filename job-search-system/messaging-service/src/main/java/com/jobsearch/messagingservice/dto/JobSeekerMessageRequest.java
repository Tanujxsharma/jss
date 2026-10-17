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
@Schema(description = "Request payload for a job seeker sending a message to an employer")
public class JobSeekerMessageRequest {

    @NotNull(message = "Employer ID is required")
    @Schema(description = "ID of the recipient employer", example = "10")
    private Long employerId;

    @NotNull(message = "Job seeker ID is required")
    @Schema(description = "ID of the job seeker sending the message", example = "101")
    private Long jobSeekerId;

    @NotNull(message = "Job ID is required")
    @Schema(description = "ID of the job opening related to this message", example = "500")
    private Long jobId;

    @NotBlank(message = "Subject is required")
    @Schema(description = "Subject line of the message", example = "Inquiry regarding Senior Java Developer position")
    private String subject;

    @NotBlank(message = "Message description is required")
    @Schema(description = "Body content of the message", example = "Thank you for considering my application. I would be happy to interview on Thursday.")
    private String messageDescription;
}
