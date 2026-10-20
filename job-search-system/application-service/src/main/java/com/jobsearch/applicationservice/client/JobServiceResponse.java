package com.jobsearch.applicationservice.client;

import lombok.Data;

@Data
public class JobServiceResponse {
    private Long id;
    private Long employerId;
    private Long employerUserId;
}
