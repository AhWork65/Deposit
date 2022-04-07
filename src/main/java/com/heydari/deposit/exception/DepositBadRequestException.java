package com.heydari.deposit.exception;

public class DepositBadRequestException extends RuntimeException{
    public DepositBadRequestException() {
        super();
    }
    public DepositBadRequestException(String message) {
        super(message);
    }
    public DepositBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
