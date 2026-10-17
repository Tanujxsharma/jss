package com.jobsearch.applicationservice.exception;

public class DuplicateApplicationException extends RuntimeException {

    public DuplicateApplicationException(String message) {
        super(message);
    }

    public DuplicateApplicationException(Long jobId, Long jobSeekerId) {
        super("Job seeker with ID " + jobSeekerId + " has already applied for job with ID " + jobId);
    }
}
