package com.datdeveloper.jdbb.exception;

public class ArgumentException extends RuntimeException {
    String message;
    public ArgumentException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
