package com.jobsearch.basketservice.exception;

public class DuplicateBasketException extends RuntimeException {
    public DuplicateBasketException(String message) {
        super(message);
    }
}
