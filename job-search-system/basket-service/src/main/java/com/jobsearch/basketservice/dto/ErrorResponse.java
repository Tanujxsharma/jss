package com.jobsearch.basketservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response payload")
public class ErrorResponse {

    @Schema(description = "Timestamp of the error")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "HTTP error phrase", example = "Not Found")
    private String error;

    @Schema(description = "Error description message", example = "Basket item not found with id: 1")
    private String message;

    @Schema(description = "Request URI path", example = "/api/basket/1")
    private String path;

    @Schema(description = "Detailed validation field errors if applicable")
    private Map<String, String> validationErrors;
}
