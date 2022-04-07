package com.heydari.deposit.exception;

public class DepositNotFoundException extends RuntimeException{

    public DepositNotFoundException() {
        super();
    }
    public DepositNotFoundException(String message) {
        super(message);
    }
    public DepositNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}