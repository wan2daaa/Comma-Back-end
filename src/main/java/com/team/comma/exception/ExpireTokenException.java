package com.team.comma.exception;

public class ExpireTokenException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public ExpireTokenException(String message) {
        super(message);
    }
}
