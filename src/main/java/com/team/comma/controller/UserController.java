package com.team.comma.controller;

import com.team.comma.dto.LoginRequest;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.RegisterRequest;
import com.team.comma.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountException;


@RestController
@RequiredArgsConstructor
public class UserController {

	final private UserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<MessageResponse> loginUser(@RequestBody LoginRequest login) throws AccountException {
		return ResponseEntity.ok().body(userService.login(login));
	}
	
	@PostMapping("/register")
	public ResponseEntity<MessageResponse> registerUser(@RequestBody RegisterRequest register) throws AccountException {
		return ResponseEntity.ok().body(userService.register(register));
	}

	/*
	@Operation(summary = "AccessToken으로 사용자 정보 가져오기", description = "Cookie에 존재하는 accessToken으로 사용자 정보를 조회")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200" , description = "문자열로 사용자 정보 반환" , content = @Content(schema = @Schema(implementation = String.class))) ,
			@ApiResponse(responseCode = "400" , description = "AccessToken이 없을 때" , content = @Content(schema = @Schema(implementation = MessageResponse.class)))
	})
	@GetMapping("/user/privacy")
	public ResponseEntity<User> getUserInfoByEmail(@CookieValue("accessToken") String accessToken) throws AccountException {
		return ResponseEntity.ok().body(userService.getUserByCookie(accessToken));
	}
	*/

}
