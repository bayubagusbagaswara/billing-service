package com.bayu.billingservice.exception;

public class GeneralException extends RuntimeException {

    public GeneralException() {
        super();
    }

    public GeneralException(String message) {
        super(message);
    }

    public GeneralException(String message, Throwable cause) {
        super(message, cause);
    }
}
