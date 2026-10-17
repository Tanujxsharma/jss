package com.jobsearch.basketservice.dto;

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
@Schema(description = "Response payload representing a saved job in a seeker basket")
public class BasketResponse {

    @Schema(description = "Unique basket entry ID", example = "1")
    private Long basketId;

    @Schema(description = "ID of the job seeker", example = "101")
    private Long jobSeekerId;

    @Schema(description = "ID of the saved job", example = "202")
    private Long jobId;

    @Schema(description = "Date and time when the job was added to the basket")
    private LocalDateTime addedDate;
}
