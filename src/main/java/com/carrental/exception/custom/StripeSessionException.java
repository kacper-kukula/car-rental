package com.carrental.exception.custom;

public class StripeSessionException extends RuntimeException {

    public StripeSessionException(String message) {
        super(message);
    }
}
