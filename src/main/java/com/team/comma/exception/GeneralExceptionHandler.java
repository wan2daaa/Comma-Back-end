package com.team.comma.exception;

import com.team.comma.dto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;

import static com.team.comma.constant.ResponseCode.*;


@RestControllerAdvice
public class GeneralExceptionHandler {

	/*
	 * 토큰 변조 , 사용자를 찾을 수 없을 때 , 사용자가 이미 존재하거나 정보가 일치하지 않을 때
	 */
	@ExceptionHandler({FalsifyTokenException.class , UsernameNotFoundException.class , AccountException.class})
	public ResponseEntity<MessageResponse> handleBadRequest(Exception e) {
		MessageResponse message = MessageResponse.of(SIMPLE_REQUEST_FAILURE , e.getMessage());

		return ResponseEntity.badRequest().body(message);
	}
	
	/*
	 * OAuth2.0 존재하지 않은 이메일
	 */
	@ExceptionHandler({AccountNotFoundException.class})
	public ResponseEntity<MessageResponse> handleAccountExcepteption(Exception e) {
		MessageResponse message = MessageResponse.of(OAUTH_NO_EXISTENT_EMAIL , e.getMessage());

		return ResponseEntity.badRequest().body(message);
	}
	/*
	 *  Spotify 예외
	 */
	@ExceptionHandler({SpotifyException.class , UnauthorizedException.class })
	public ResponseEntity<MessageResponse> handleSpotifyException(Exception e) {
		MessageResponse message = MessageResponse.of(SPOTIFY_FAILURE , e.getMessage());

		return ResponseEntity.internalServerError().body(message);
	}

}