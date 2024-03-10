package com.bayu.billingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CalculateBillingException extends RuntimeException {

    public CalculateBillingException() {
        super();
    }

    public CalculateBillingException(String message) {
        super(message);
    }
}
