package com.team.comma.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.team.comma.dto.MessageResponse;
import com.team.comma.entity.RefreshToken;
import com.team.comma.entity.Token;
import com.team.comma.exception.FalsifyTokenException;
import com.team.comma.repository.RefreshTokenRepository;
import com.team.comma.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

	@Mock
	RefreshTokenRepository refreshTokenRepository;

	@Mock
	JwtTokenProvider jwtTokenProvider;

	@InjectMocks
	JwtService jwtService;

	@Test
	@DisplayName("토큰 생성")
	public void createToken() {
		// given
		Token token = getToken();
		RefreshToken refreshToken = getRefreshToken();
		doReturn(refreshToken).when(refreshTokenRepository).existsByKeyEmail(refreshToken.getKeyEmail());
		doNothing().when(refreshTokenRepository).deleteByKeyEmail(refreshToken.getKeyEmail());
		doReturn(null).when(refreshTokenRepository).save(any(RefreshToken.class));

		// when
		Throwable thrown = catchThrowable(() -> jwtService.login(token));

		// then
		assertThat(thrown).doesNotThrowAnyException();
	}
	
	@Test
	@DisplayName("AccessToken 토큰 반환")
	public void createAccessToken() {
		// given
		RefreshToken refreshToken = getRefreshToken();
		Optional<RefreshToken> tokens = Optional.of(refreshToken);
		doReturn(tokens).when(refreshTokenRepository).findByRefreshToken(refreshToken.getRefreshToken());
		doReturn("Token").when(jwtTokenProvider).validateRefreshToken(any(RefreshToken.class));
		
		// when
		MessageResponse result = jwtService.validateRefreshToken(refreshToken.getRefreshToken());
		
		// then
		assertThat(result.getCode()).isEqualTo(7);
		assertThat(result.getMessage()).isEqualTo("Token");
	}
	
	@Test
	@DisplayName("만료된 RefreshToken 반환")
	public void expiretoken() {
		// given
		RefreshToken refreshToken = getRefreshToken();
		Optional<RefreshToken> tokens = Optional.of(refreshToken);
		doReturn(tokens).when(refreshTokenRepository).findByRefreshToken(refreshToken.getRefreshToken());
		doReturn(null).when(jwtTokenProvider).validateRefreshToken(any(RefreshToken.class));
		
		// when
		MessageResponse result = jwtService.validateRefreshToken(refreshToken.getRefreshToken());
		
		// then
		assertThat(result.getCode()).isEqualTo(-7);
		assertThat(result.getMessage()).isEqualTo("Refresh 토큰이 만료되었습니다. 로그인이 필요합니다.");
	}
	
	@Test
	@DisplayName("변조된 RefreshToken 예외")
	public void falsifytoken() {
		// given
		RefreshToken refreshToken = getRefreshToken();
		doThrow(NoSuchElementException.class).when(refreshTokenRepository).findByRefreshToken(refreshToken.getRefreshToken());
		
		// when
		Throwable thrown = catchThrowable(() -> jwtService.validateRefreshToken(refreshToken.getRefreshToken()));
		
		// then
		assertThat(thrown).isInstanceOf(FalsifyTokenException.class).hasMessage("변조되거나, 알 수 없는 RefreshToken 입니다.");
		
	}

	public RefreshToken getRefreshToken() {
		return RefreshToken.builder().keyEmail("keyEmail").refreshToken("refreshToken").build();
	}

	public Token getToken() {
		return Token.builder().key("keyEmail").refreshToken("refreshToken").build();
	}

}
