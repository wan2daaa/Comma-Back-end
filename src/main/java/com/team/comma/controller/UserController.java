package com.team.comma.controller;

import javax.security.auth.login.AccountException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.team.comma.dto.MessageDTO;
import com.team.comma.dto.RequestUserDTO;
import com.team.comma.dto.TokenDTO;
import com.team.comma.service.UserService;

import jakarta.servlet.http.HttpServletResponse;


@RestController
public class UserController {

	@Autowired UserService userService;
	
	@RequestMapping(value = "/login" , method = RequestMethod.POST)
	public TokenDTO login(@RequestBody RequestUserDTO userDTO , HttpServletResponse response) throws AccountException {
		return userService.login(userDTO , response);
	}
	
	@RequestMapping(value = "/register" , method = RequestMethod.POST)
	public MessageDTO register(@RequestBody RequestUserDTO userDTO) throws AccountException {
		return userService.register(userDTO);
	}
	
}
