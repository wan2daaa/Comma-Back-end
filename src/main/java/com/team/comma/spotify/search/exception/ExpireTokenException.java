package com.team.comma.spotify.search.exception;

public class ExpireTokenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExpireTokenException(String message) {
        super(message);
    }
}
