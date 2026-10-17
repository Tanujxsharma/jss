package com.jobsearch.employerservice.exception;

public class DuplicateEmployerException extends RuntimeException {
    public DuplicateEmployerException(String message) {
        super(message);
    }
}
