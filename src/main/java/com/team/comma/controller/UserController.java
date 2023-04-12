package com.team.comma.controller;

import javax.security.auth.login.AccountException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.team.comma.dto.LoginRequest;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.RegisterRequest;
import com.team.comma.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class UserController {

	final private UserService userService;
	
	@PostMapping(value = "/login")
	public MessageResponse loginUser(@RequestBody LoginRequest login) throws AccountException {
		return userService.login(login);
	}
	
	@PostMapping(value = "/register")
	public MessageResponse registerUser(@RequestBody RegisterRequest register) throws AccountException {
		return userService.register(register);
	}

}
