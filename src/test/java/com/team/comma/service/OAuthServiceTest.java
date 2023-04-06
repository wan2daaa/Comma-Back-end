package com.team.comma.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AccountException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.OAuthRequest;
import com.team.comma.oauth.RegisterationOAuthUser;
import com.team.comma.oauth.IssuanceAccessToken;

@ExtendWith(MockitoExtension.class)
public class OAuthServiceTest {

	@InjectMocks
	OAuthService oauthService;

	@Mock
	RegisterationOAuthUser createOAuthUser;

	@Mock
	IssuanceAccessToken getAccessToken;
	
	final private String userEmail = "example@naver.com";
	
	 
	// @MethodSource("oauthServerParameter") @ParameterizedTest
	@Test
	@DisplayName("google 정보 요청 예외 _ 유효하지 않은 code")
	public void rejectGoogleOauthInfo_invalidCode() {
		// given
		doThrow(new NullPointerException()).when(getAccessToken).getGoogleAccessToken(any(String.class));
		OAuthRequest oauthRequest = getOauthRequest("google");
		
		// when
		Throwable thrown = catchThrowable(() -> oauthService.loginOAuthServer(oauthRequest));
		
		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("유효하지 않은 접근입니다.");
	}
	
	@Test
	@DisplayName("naver 정보 요청 예외 _ 유효하지 않은 code")
	public void rejectNaverOauthInfo_invalidCode() {
		// given
		doThrow(new NullPointerException()).when(getAccessToken).getNaverAccessToken(any(String.class) , any(String.class));
		OAuthRequest oauthRequest = getOauthRequest("naver");
		
		// when
		Throwable thrown = catchThrowable(() -> oauthService.loginOAuthServer(oauthRequest));
		
		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("유효하지 않은 접근입니다.");
	}
	
	@Test
	@DisplayName("kakao 정보 요청 예외 _ 유효하지 않은 code")
	public void rejectKakaoOauthInfo_invalidCode() {
		// given
		doThrow(new NullPointerException()).when(getAccessToken).getKakaoAccessToken(any(String.class));
		OAuthRequest oauthRequest = getOauthRequest("kakao");
		
		// when
		Throwable thrown = catchThrowable(() -> oauthService.loginOAuthServer(oauthRequest));
		
		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("유효하지 않은 접근입니다.");
	}
	
	@Test
	@DisplayName("OAuth 정보 요청 예외 _ 유효하지 않은 소셜 서버")
	public void rejectOauthInfo_invalidServer() throws AccountException {
		// given
		OAuthRequest oauthRequest = getOauthRequest("server");
		
		// when
		MessageResponse result = oauthService.loginOAuthServer(oauthRequest);
		
		// then
		assertThat(result.getCode()).isEqualTo(-1);
		assertThat(result.getMessage()).isEqualTo("잘못된 소셜서버입니다.");
	}
	
	@Test
	@DisplayName("google 로그인 성공")
	public void successGoogleLogin() throws JsonMappingException, JsonProcessingException, AccountException {
		// given
		OAuthRequest oauthRequest = getOauthRequest("google");
		JsonNode json = getJsonNode();
		MessageResponse message = new MessageResponse(1 , "로그인이 성공적으로 되었습니다." , userEmail);
		doReturn(json).when(getAccessToken).getGoogleAccessToken(any(String.class));
		doReturn(message).when(createOAuthUser).createGoogleUser(any(String.class));
		
		// when
		MessageResponse result = oauthService.loginOAuthServer(oauthRequest);
		
		// then
		assertThat(result.getCode()).isEqualTo(1);
		assertThat(result.getMessage()).isEqualTo("로그인이 성공적으로 되었습니다.");
		
		// verify
		verify(createOAuthUser , times(1)).createGoogleUser(any(String.class));
		verify(getAccessToken , times(1)).getGoogleAccessToken(any(String.class));
	}
	
	@Test
	@DisplayName("naver 로그인 성공")
	public void successNaverLogin() throws JsonMappingException, JsonProcessingException, AccountException {
		// given
		OAuthRequest oauthRequest = getOauthRequest("naver");
		JsonNode json = getJsonNode();
		MessageResponse message = new MessageResponse(1 , "로그인이 성공적으로 되었습니다." , userEmail);
		doReturn(json).when(getAccessToken).getNaverAccessToken(any(String.class) , any(String.class));
		doReturn(message).when(createOAuthUser).createNaverUser(any(String.class));
		
		// when
		MessageResponse result = oauthService.loginOAuthServer(oauthRequest);
		
		// then
		assertThat(result.getCode()).isEqualTo(1);
		assertThat(result.getMessage()).isEqualTo("로그인이 성공적으로 되었습니다.");
		
		// verify
		verify(createOAuthUser , times(1)).createNaverUser(any(String.class));
		verify(getAccessToken , times(1)).getNaverAccessToken(any(String.class) , any(String.class));
	}
	
	@Test
	@DisplayName("kakao 로그인 성공")
	public void successKakaoLogin() throws JsonMappingException, JsonProcessingException, AccountException {
		// given
		OAuthRequest oauthRequest = getOauthRequest("kakao");
		JsonNode json = getJsonNode();
		MessageResponse message = new MessageResponse(1 , "로그인이 성공적으로 되었습니다." , userEmail);
		doReturn(json).when(getAccessToken).getKakaoAccessToken(any(String.class));
		doReturn(message).when(createOAuthUser).createKakaoUser(any(String.class));
		
		// when
		MessageResponse result = oauthService.loginOAuthServer(oauthRequest);
		
		// then
		assertThat(result.getCode()).isEqualTo(1);
		assertThat(result.getMessage()).isEqualTo("로그인이 성공적으로 되었습니다.");
		
		// verify
		verify(createOAuthUser , times(1)).createKakaoUser(any(String.class));
		verify(getAccessToken , times(1)).getKakaoAccessToken(any(String.class));
	}
	
	public OAuthRequest getOauthRequest(final String server) {
		return OAuthRequest.builder()
				.type(server)
				.code("code")
				.state("state")
				.build();
	}
	
	public JsonNode getJsonNode() throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, String> map = new HashMap<>();
        map.put("access_token", "token");
		
		return mapper.valueToTree(map);
	}
	
}
