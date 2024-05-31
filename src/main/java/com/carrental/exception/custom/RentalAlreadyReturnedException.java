package com.carrental.exception.custom;

public class RentalAlreadyReturnedException extends RuntimeException {

    public RentalAlreadyReturnedException(String message) {
        super(message);
    }
}
