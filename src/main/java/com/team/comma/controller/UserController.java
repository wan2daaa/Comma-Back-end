package com.team.comma.controller;

import javax.security.auth.login.AccountException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.team.comma.dto.LoginRequest;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.OAuthRequest;
import com.team.comma.dto.RegisterRequest;
import com.team.comma.service.OAuthService;
import com.team.comma.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class UserController {

	final private UserService userService;
	final private OAuthService oauthService;
	
	@PostMapping(value = "/login")
	public MessageResponse loginUser(@RequestBody LoginRequest login) throws AccountException {
		return userService.login(login);
	}
	
	@PostMapping(value = "/register")
	public MessageResponse registerUser(@RequestBody RegisterRequest register) throws AccountException {
		return userService.register(register);
	}
	
	@Operation(summary = "OAuth 로그인 API", description = "해당 서버 API에 code와 state를 전달하여 로그인 처리")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "로그인 성공 시 사용자 아이디를 반환", content = @Content(schema = @Schema(implementation = MessageResponse.class))),
	        @ApiResponse(responseCode = "400", description = "code , state , type 중 1개 누락", content = @Content(schema = @Schema(implementation = MessageResponse.class))),
	        @ApiResponse(responseCode = "500", description = "소셜 서버 및 서버에서 오류 발생", content = @Content(schema = @Schema(implementation = MessageResponse.class)))
	    })
	@PostMapping(value = "/oauth/login")
	public MessageResponse loginOAuthUser(@RequestBody OAuthRequest oauthRequest) throws AccountException {
		return oauthService.loginOAuthServer(oauthRequest);
	}
}