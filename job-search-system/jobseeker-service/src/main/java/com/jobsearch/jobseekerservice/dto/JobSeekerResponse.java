package com.jobsearch.jobseekerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerResponse {

    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String location;
    private String skills;
    private BigDecimal experienceYears;
    private String resumeUrl;
    private String summary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
