package com.team.comma.spotify.search.exception;

public class TokenExpirationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenExpirationException(String message) {
        super(message);
    }
}
