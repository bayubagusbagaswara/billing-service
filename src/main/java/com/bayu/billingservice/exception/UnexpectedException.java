package com.bayu.billingservice.exception;

public class UnexpectedException extends RuntimeException {

    public UnexpectedException() {
        super();
    }

    public UnexpectedException(String message) {
        super(message);
    }
}
