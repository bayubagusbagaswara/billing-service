package com.bayu.billingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BillingCalculationException extends RuntimeException {

    public BillingCalculationException() {
        super();
    }

    public BillingCalculationException(String message) {
        super(message);
    }
}
