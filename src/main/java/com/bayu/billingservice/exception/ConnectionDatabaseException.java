package com.bayu.billingservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ConnectionDatabaseException extends RuntimeException {

    public ConnectionDatabaseException() {
        super();
    }

    public ConnectionDatabaseException(String message) {
        super(message);
    }

}
