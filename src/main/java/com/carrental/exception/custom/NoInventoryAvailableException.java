package com.carrental.exception.custom;

public class NoInventoryAvailableException extends RuntimeException {

    public NoInventoryAvailableException(String message) {
        super(message);
    }
}
