package com.team.comma.exception;

import javax.security.auth.login.AccountException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.team.comma.dto.MessageDTO;


@RestControllerAdvice
public class GeneralExceptionHandler {

	/*
	 * 토큰 변조 , 사용자를 찾을 수 없을 때 , 사용자가 이미 존재하거나 정보가 일치하지 않을 때
	 */
	@ExceptionHandler({FalsifyTokenException.class , UsernameNotFoundException.class , AccountException.class })
	public MessageDTO handleBadRequest(Exception e) {
		return MessageDTO.builder()
				.code(-1)
				.message(e.getMessage())
				.build();
	}
	
}