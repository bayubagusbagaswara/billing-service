package com.bayu.billingservice.exception;

public class BillingCalculationException extends RuntimeException {

    public BillingCalculationException() {
        super();
    }

    public BillingCalculationException(String message) {
        super(message);
    }

    public BillingCalculationException(String message, Throwable cause) {
        super(message, cause);
    }

}
