package com.team.comma.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import javax.security.auth.login.AccountException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;
import com.team.comma.dto.LoginRequest;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.RegisterRequest;
import com.team.comma.exception.GeneralExceptionHandler;
import com.team.comma.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	@InjectMocks
	UserController userController;

	@Mock
	UserService userService;

	MockMvc mockMvc;
	Gson gson;
	private String userEmail = "email@naver.com";
	private String userPassword = "password";

	@BeforeEach
	public void init() {
		gson = new Gson();
		mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(new GeneralExceptionHandler()) // GeneralException 사용
				.build();
	}

	@Test
	@DisplayName("로그인 요청 성공")
	public void loginUser() throws Exception {
		// given
		final String api = "/login";
		final LoginRequest request = getLoginRequest();
		final MessageResponse message = MessageResponse.builder().code(1).message("로그인이 성공적으로 되었습니다.").data(request.getEmail()).build();
		doReturn(message).when(userService).login(any(LoginRequest.class));

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(api).content(gson.toJson(request)).contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isOk());

		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);
		assertThat(response.getCode()).isEqualTo(1);
		assertThat(response.getMessage()).isEqualTo("로그인이 성공적으로 되었습니다.");
		assertThat(response.getData()).isEqualTo(request.getEmail());
	}

	@Test
	@DisplayName("로그인 요청 실패_올바르지 않은 정보")
	public void notExistUser() throws Exception {
		// given
		final String api = "/login";
		LoginRequest request = getLoginRequest();
		AccountException exception = new AccountException("정보가 올바르지 않습니다.");
		doThrow(exception).when(userService).login(any(LoginRequest.class));

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(api).content(gson.toJson(request)).contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isOk());
		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);

		assertThat(response.getCode()).isEqualTo(-1);
		assertThat(response.getMessage()).isEqualTo("정보가 올바르지 않습니다.");
	}

	@Test
	@DisplayName("사용자 회원가입 성공")
	public void registUser() throws Exception {
		// given
		final String api = "/register";
		LoginRequest request = getLoginRequest();
		doReturn(MessageResponse.builder().code(1).message("성공적으로 가입되었습니다.").build()).when(userService)
				.register(any(RegisterRequest.class));

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(api).content(gson.toJson(request)).contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isOk());
		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);
		
		assertThat(response.getCode()).isEqualTo(1);
		assertThat(response.getMessage()).isEqualTo("성공적으로 가입되었습니다.");
	}
	
	@Test
	@DisplayName("사용자 회원가입 실패_이미 존재하는 회원")
	public void existUserException() throws Exception {
		// given
		final String api = "/register";
		LoginRequest request = getLoginRequest();
		doThrow(new AccountException("이미 존재하는 계정입니다.")).when(userService)
				.register(any(RegisterRequest.class));

		// when
		final ResultActions resultActions = mockMvc.perform(
				MockMvcRequestBuilders.post(api).content(gson.toJson(request)).contentType(MediaType.APPLICATION_JSON));

		// then
		resultActions.andExpect(status().isOk());
		final MessageResponse response = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);
		
		assertThat(response.getCode()).isEqualTo(-1);
		assertThat(response.getMessage()).isEqualTo("이미 존재하는 계정입니다.");
	}
	
	public LoginRequest getLoginRequest() {
		return LoginRequest.builder()
				.email(userEmail)
				.password(userPassword)
				.build();
	}

}