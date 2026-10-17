package com.jobsearch.messagingservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload representing a message sent by an employer")
public class EmployerMessageResponse {

    @Schema(description = "Unique message ID", example = "1")
    private Long messageId;

    @Schema(description = "ID of the sending employer", example = "10")
    private Long employerId;

    @Schema(description = "ID of the recipient job seeker", example = "101")
    private Long jobSeekerId;

    @Schema(description = "ID of the related job", example = "500")
    private Long jobId;

    @Schema(description = "Subject line of the message", example = "Interview Invitation - Senior Java Developer")
    private String subject;

    @Schema(description = "Body content of the message", example = "We were impressed with your application and would like to schedule an interview.")
    private String messageDescription;

    @Schema(description = "Timestamp when the message was sent")
    private LocalDateTime sentDate;
}
