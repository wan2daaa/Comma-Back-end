package com.team.comma.exception;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.team.comma.dto.MessageResponse;

import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;


@RestControllerAdvice
public class GeneralExceptionHandler {

	/*
	 * 토큰 변조 , 사용자를 찾을 수 없을 때 , 사용자가 이미 존재하거나 정보가 일치하지 않을 때
	 */
	@ExceptionHandler({FalsifyTokenException.class , UsernameNotFoundException.class , AccountException.class })
	public MessageResponse handleBadRequest(Exception e) {
		return MessageResponse.builder()
				.code(-1)
				.message(e.getMessage())
				.build();
	}
	
	/*
	 * OAuth2.0 존재하지 않은 이메일
	 */
	@ExceptionHandler({AccountNotFoundException.class})
	public MessageResponse handleAccountExcepteption(Exception e) {
		return MessageResponse.builder()
				.code(-3)
				.message(e.getMessage())
				.build();
	}
	/*
	 *  Spotify 예외
	 */
	@ExceptionHandler({SpotifyException.class , UnauthorizedException.class })
	public MessageResponse handleSpotifyException(Exception e) {
		return MessageResponse.builder()
				.code(-2)
				.message(e.getMessage())
				.build();
	}
	
}