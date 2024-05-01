package com.bayu.billingservice.exception;

public class DataChangeException extends RuntimeException {

    public DataChangeException() {
        super();
    }

    public DataChangeException(String message) {
        super(message);
    }

    public DataChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
