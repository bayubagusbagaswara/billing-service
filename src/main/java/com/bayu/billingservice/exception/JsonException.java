package com.bayu.billingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JsonException extends RuntimeException{

    public JsonException() {
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
