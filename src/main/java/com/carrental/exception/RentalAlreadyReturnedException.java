package com.carrental.exception;

public class RentalAlreadyReturnedException extends RuntimeException {

    public RentalAlreadyReturnedException(String message) {
        super(message);
    }
}
