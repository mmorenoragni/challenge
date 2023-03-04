package com.example.challenge.exceptions;

public class RateLimitException extends RuntimeException {

    private final int httpStatusCode = 429;
    public RateLimitException(String msg) {
        super(msg);
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
