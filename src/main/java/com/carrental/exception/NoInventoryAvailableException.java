package com.carrental.exception;

public class NoInventoryAvailableException extends RuntimeException {

    public NoInventoryAvailableException(String message) {
        super(message);
    }
}
