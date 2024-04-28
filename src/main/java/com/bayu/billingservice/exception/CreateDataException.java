package com.bayu.billingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CreateDataException extends RuntimeException {

    public CreateDataException() {
        super();
    }

    public CreateDataException(String message) {
        super(message);
    }

    public CreateDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
