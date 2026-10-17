package com.jobsearch.applicationservice.exception;

public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(String message) {
        super(message);
    }

    public JobNotFoundException(Long jobId) {
        super("Job not found with ID: " + jobId);
    }
}
