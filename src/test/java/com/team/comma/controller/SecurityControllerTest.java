package com.team.comma.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;
import com.team.comma.dto.MessageDTO;
import com.team.comma.service.JwtService;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
public class SecurityControllerTest {

	@InjectMocks
	SecurityController controller;

	@Mock
	JwtService jwtService;

	MockMvc mockMvc;
	Gson gson;

	@BeforeEach
	public void setup() {
		gson = new Gson();
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	@DisplayName("인증되지 않은 사용자 처리")
	public void deniedAuthenticationUser() throws Exception {
		// given
		final String api = "/authentication/denied";

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));

		// then
		resultActions.andExpect(status().isOk());
		final MessageDTO messageDTO = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageDTO.class);

		assertThat(messageDTO.getCode()).isEqualTo(-1);
		assertThat(messageDTO.getMessage()).isEqualTo("인증되지 않은 사용자입니다.");
	}

	@Test
	@DisplayName("새로운 Access 토큰 발행")
	public void createNewAccessToken() throws Exception {
		// given
		final String api = "/authentication/denied";
		doReturn(MessageDTO.builder().code(7).message("token").build()).when(jwtService).validateRefreshToken("token");
		Cookie cookie = new Cookie("refreshToken", "token");

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api).cookie(cookie));

		// then
		resultActions.andExpect(status().isOk());
		final MessageDTO messageDTO = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageDTO.class);

		assertThat(messageDTO.getCode()).isEqualTo(7);
		assertThat(messageDTO.getMessage()).isEqualTo("token");
	}

	@Test
	@DisplayName("인가되지 않은 사용자")
	public void deniedAuthorizationUser() throws Exception {
		// given
		final String api = "/authorization/denied";

		// when
		final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));

		// then
		resultActions.andExpect(status().isOk());
		MessageDTO result = gson.fromJson(
				resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageDTO.class);

		assertThat(result.getCode()).isEqualTo(-1);
		assertThat(result.getMessage()).isEqualTo("인가되지 않은 사용자입니다.");
		
	}

}
