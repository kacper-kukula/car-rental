package com.carrental.exception;

public class UnauthorizedViewException extends RuntimeException {

    public UnauthorizedViewException(String message) {
        super(message);
    }
}
