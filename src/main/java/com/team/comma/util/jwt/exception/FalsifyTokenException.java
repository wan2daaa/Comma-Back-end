package com.team.comma.util.jwt.exception;

public class FalsifyTokenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FalsifyTokenException(String message) {
        super(message);
    }
}