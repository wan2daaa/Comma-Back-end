package com.team.comma.controller;

import com.team.comma.dto.*;
import com.team.comma.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;


@RestController
@RequiredArgsConstructor
public class UserController {

	final private UserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<MessageResponse> loginUser(@RequestBody LoginRequest login) throws AccountException {
		return userService.login(login);
	}
	
	@PostMapping("/register")
	public ResponseEntity<MessageResponse> registerUser(@RequestBody RegisterRequest register) throws AccountException {
		return ResponseEntity.ok().body(userService.register(register));
	}

	@PostMapping("/private-information")
	public ResponseEntity<MessageResponse> createUserInformation(@CookieValue(value = "accessToken" , required = false) String accessToken
			, @RequestBody UserDetailRequest userDetail) throws AccountException {
		return userService.createUserInformation(userDetail , accessToken);
	}

	@GetMapping("/user/information")
	public ResponseEntity<UserResponse> getUserInfoByEmail(@CookieValue("accessToken") String accessToken) throws AccountException {
		return ResponseEntity.ok().body(userService.getUserByCookie(accessToken));
	}

}
