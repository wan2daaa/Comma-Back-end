package com.team.comma.controller;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.comma.dto.MessageDTO;
import com.team.comma.service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SecurityController {
	
	final private JwtService jwtService;

	@GetMapping(value = "/authentication/denied")
	public MessageDTO authenticationDenied(@CookieValue(name = "refreshToken" , required = false) String authorization) {
		if(authorization == null) {
			return MessageDTO.builder()
					.code(-1)
					.message("인증되지 않은 사용자입니다.")
					.build();
		}
		
		return jwtService.validateRefreshToken(authorization);
		
	}
	
	@GetMapping(value = "/authorization/denied")
	public MessageDTO authorizationDenied() {
		return MessageDTO.builder()
				.code(-1)
				.message("인가되지 않은 사용자입니다.")
				.build();
	}
	
}
