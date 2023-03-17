package com.team.comma.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.comma.dto.MessageDTO;
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
	public void login(Token tokenDto) {

		RefreshToken refreshToken = RefreshToken.builder().keyEmail(tokenDto.getKey())
				.refreshToken(tokenDto.getRefreshToken()).build();
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

	public MessageDTO validateRefreshToken(String refreshToken) {
		try {
			RefreshToken refreshToken1 = getRefreshToken(refreshToken).get();
			String createdAccessToken = jwtTokenProvider.validateRefreshToken(refreshToken1);

			return createRefreshJson(createdAccessToken);
		} catch (NoSuchElementException e) {
			throw new FalsifyTokenException("변조되거나, 알 수 없는 RefreshToken 입니다.");
		}
	}

	public MessageDTO createRefreshJson(String createdAccessToken) {
		if (createdAccessToken == null) {
			return MessageDTO.builder()
					.code(-7)
					.message("Refresh 토큰이 만료되었습니다. 로그인이 필요합니다.")
					.build();
		}
		
		return MessageDTO.builder()
				.code(7)
				.message(createdAccessToken)
				.build();
	}

	public JwtService() {

	}
}
