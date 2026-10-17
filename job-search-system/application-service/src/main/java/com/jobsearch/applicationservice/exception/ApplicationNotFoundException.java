package com.jobsearch.applicationservice.exception;

public class ApplicationNotFoundException extends RuntimeException {

    public ApplicationNotFoundException(String message) {
        super(message);
    }

    public ApplicationNotFoundException(Long id) {
        super("Application not found with ID: " + id);
    }
}
