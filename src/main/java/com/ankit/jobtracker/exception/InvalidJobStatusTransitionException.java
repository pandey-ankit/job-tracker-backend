package com.ankit.jobtracker.exception;

public class InvalidJobStatusTransitionException extends RuntimeException {

    public InvalidJobStatusTransitionException(String message) {
        super(message);
    }
}