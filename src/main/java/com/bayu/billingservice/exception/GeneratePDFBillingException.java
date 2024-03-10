package com.bayu.billingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GeneratePDFBillingException extends RuntimeException {

    public GeneratePDFBillingException() {
    }

    public GeneratePDFBillingException(String message) {
        super(message);
    }
}
