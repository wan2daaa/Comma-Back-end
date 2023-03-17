package com.team.comma.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.team.comma.dto.MessageDTO;
import com.team.comma.service.JwtService;

@RestController
public class SecurityController {
	
	@Autowired JwtService jwtService;

	@RequestMapping(value = "/authentication/denied" , method = RequestMethod.GET)
	public MessageDTO authenticationDenied(@CookieValue(name = "refreshToken" , required = false) String authorization) {
		if(authorization == null) {
			return MessageDTO.builder()
					.code(-1)
					.message("인증되지 않은 사용자입니다.")
					.build();
		}
		
		return jwtService.validateRefreshToken(authorization);
		
	}
	
	@RequestMapping(value = "/authorization/denied" , method = RequestMethod.GET)
	public MessageDTO authorizationDenied() {
		return MessageDTO.builder()
				.code(-1)
				.message("인가되지 않은 사용자입니다.")
				.build();
	}
	
	@RequestMapping(value = "/security" , method = RequestMethod.GET)
	public String userRequest() {
		return "인증 요청";
	}
	
	
}
