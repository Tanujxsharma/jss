package com.jobsearch.employerservice.dto;

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
@Schema(description = "Response object representing an employer profile")
public class EmployerResponse {

    @Schema(description = "Unique identifier of the employer profile", example = "1")
    private Long id;

    @Schema(description = "User ID associated with the employer account", example = "101")
    private Long userId;

    @Schema(description = "Name of the organization", example = "Acme Corporation")
    private String organizationName;

    @Schema(description = "Email address of the employer organization", example = "careers@acme.com")
    private String email;

    @Schema(description = "10-digit contact number", example = "9876543210")
    private String contactNumber;

    @Schema(description = "Location / headquarters", example = "San Francisco, CA")
    private String location;

    @Schema(description = "About the organization", example = "Leading provider of technology solutions.")
    private String about;

    @Schema(description = "Organization website URL", example = "https://www.acme.com")
    private String website;

    @Schema(description = "Timestamp when the employer profile was created")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the employer profile was last updated")
    private LocalDateTime updatedAt;
}
