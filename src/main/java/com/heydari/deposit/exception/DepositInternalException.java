package com.heydari.deposit.exception;

public class DepositInternalException extends  Exception{
    public DepositInternalException() {
        super();
    }
    public DepositInternalException(String message) {
        super(message);
    }

    public DepositInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
