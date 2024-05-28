package com.bayu.billingservice.exception;

public class ReadExcelException extends RuntimeException {

    public ReadExcelException() {
        super();
    }

    public ReadExcelException(String message) {
        super(message);
    }

    public ReadExcelException(String message, Throwable cause) {
        super(message, cause);
    }

}
