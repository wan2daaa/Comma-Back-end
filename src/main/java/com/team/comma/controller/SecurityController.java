package com.team.comma.controller;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.comma.dto.MessageResponse;
import com.team.comma.service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SecurityController {
	
	final private JwtService jwtService;

	@GetMapping(value = "/authentication/denied")
	public MessageResponse informAuthenticationDenied(@CookieValue(name = "refreshToken" , required = false) String authorization) {
		if(authorization == null) {
			return MessageResponse.builder()
					.code(-1)
					.message("인증되지 않은 사용자입니다.")
					.build();
		}
		
		return jwtService.validateRefreshToken(authorization);
		
	}
	
	@GetMapping(value = "/authorization/denied")
	public MessageResponse informAuthorizationDenied() {
		return MessageResponse.builder()
				.code(-1)
				.message("인가되지 않은 사용자입니다.")
				.build();
	}
	
	@GetMapping(value = "/logout/message")
	public MessageResponse logoutMessage() {
		return MessageResponse.builder()
				.code(1)
				.message("로그아웃이 성공적으로 되었습니다.")
				.build();
	}
	
}
