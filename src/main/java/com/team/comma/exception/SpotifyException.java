package com.team.comma.exception;

public class SpotifyException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public SpotifyException(String message) {
		super(message);
	}
}
