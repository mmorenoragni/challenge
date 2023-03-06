package com.example.challenge.exceptions;

public class BadRequestException extends RuntimeException {

    private final int httpStatusCode = 400;

    public BadRequestException(String msg) {
        super(msg);
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
