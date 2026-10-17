package com.jobsearch.employerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for creating or updating an employer profile")
public class EmployerRequest {

    @NotNull(message = "User ID is required")
    @Schema(description = "User ID associated with the employer account", example = "101")
    private Long userId;

    @NotBlank(message = "Organization name is required")
    @Schema(description = "Name of the organization", example = "Acme Corporation")
    private String organizationName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email address of the employer organization", example = "careers@acme.com")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
    @Schema(description = "10-digit contact number", example = "9876543210")
    private String contactNumber;

    @Schema(description = "Location / headquarters", example = "San Francisco, CA")
    private String location;

    @Schema(description = "About the organization", example = "Leading provider of technology solutions.")
    private String about;

    @Schema(description = "Organization website URL", example = "https://www.acme.com")
    private String website;
}
