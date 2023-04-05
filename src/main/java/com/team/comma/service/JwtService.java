package com.team.comma.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.comma.dto.MessageResponse;
import com.team.comma.entity.RefreshToken;
import com.team.comma.entity.Token;
import com.team.comma.exception.FalsifyTokenException;
import com.team.comma.repository.RefreshTokenRepository;
import com.team.comma.security.JwtTokenProvider;

import jakarta.transaction.Transactional;

@Service
public class JwtService {
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	@Autowired
	RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public void login(Token tokenEntity) {

		RefreshToken refreshToken = RefreshToken.builder().keyEmail(tokenEntity.getKey())
				.refreshToken(tokenEntity.getRefreshToken()).build();
		String loginUserEmail = refreshToken.getKeyEmail();

		RefreshToken token = refreshTokenRepository.existsByKeyEmail(loginUserEmail);
		if (token != null) { // 기존 존재하는 토큰 제거
			refreshTokenRepository.deleteByKeyEmail(loginUserEmail);
		}
		refreshTokenRepository.save(refreshToken);

	}

	public Optional<RefreshToken> getRefreshToken(String refreshToken) {

		return refreshTokenRepository.findByRefreshToken(refreshToken);
	}

	public MessageResponse validateRefreshToken(String refreshToken) {
		try {
			RefreshToken refreshToken1 = getRefreshToken(refreshToken).get();
			String createdAccessToken = jwtTokenProvider.validateRefreshToken(refreshToken1);

			return createRefreshJson(createdAccessToken);
		} catch (NoSuchElementException e) {
			throw new FalsifyTokenException("변조되거나, 알 수 없는 RefreshToken 입니다.");
		}
	}

	public MessageResponse createRefreshJson(String createdAccessToken) {
		if (createdAccessToken == null) {
			return MessageResponse.builder()
					.code(-7)
					.message("Refresh 토큰이 만료되었습니다. 로그인이 필요합니다.")
					.build();
		}
		
		return MessageResponse.builder()
				.code(7)
				.message(createdAccessToken)
				.build();
	}

	public JwtService() {

	}
}
