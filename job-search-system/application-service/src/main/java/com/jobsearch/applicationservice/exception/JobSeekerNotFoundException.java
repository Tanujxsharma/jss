package com.jobsearch.applicationservice.exception;

public class JobSeekerNotFoundException extends RuntimeException {

    public JobSeekerNotFoundException(String message) {
        super(message);
    }

    public JobSeekerNotFoundException(Long jobSeekerId) {
        super("Job seeker not found with ID: " + jobSeekerId);
    }
}
