package com.team.comma.util.jwt.exception;

public class TokenForgeryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenForgeryException(String message) {
        super(message);
    }
}