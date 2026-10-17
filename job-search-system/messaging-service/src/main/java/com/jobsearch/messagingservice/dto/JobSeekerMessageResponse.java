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
@Schema(description = "Response payload representing a message sent by a job seeker")
public class JobSeekerMessageResponse {

    @Schema(description = "Unique message ID", example = "1")
    private Long messageId;

    @Schema(description = "ID of the recipient employer", example = "10")
    private Long employerId;

    @Schema(description = "ID of the sending job seeker", example = "101")
    private Long jobSeekerId;

    @Schema(description = "ID of the related job", example = "500")
    private Long jobId;

    @Schema(description = "Subject line of the message", example = "Inquiry regarding Senior Java Developer position")
    private String subject;

    @Schema(description = "Body content of the message", example = "Thank you for considering my application. I would be happy to interview on Thursday.")
    private String messageDescription;

    @Schema(description = "Timestamp when the message was sent")
    private LocalDateTime sentDate;
}
