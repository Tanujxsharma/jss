package com.jobsearch.basketservice.dto;

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
@Schema(description = "Request payload for adding a job to seeker basket")
public class BasketRequest {

    @NotNull(message = "Job seeker ID is required")
    @Schema(description = "ID of the job seeker", example = "101")
    private Long jobSeekerId;

    @NotNull(message = "Job ID is required")
    @Schema(description = "ID of the job to save to basket", example = "202")
    private Long jobId;
}
