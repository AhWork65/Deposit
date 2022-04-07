package com.heydari.deposit.exception;

public class DepositCreateException extends Exception{
    public DepositCreateException() {
        super();
    }
    public DepositCreateException(String message) {
        super(message);
    }

    public DepositCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
