package com.team.comma.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team.comma.entity.RefreshToken;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // EmbeddedDatabase 가 아닌 Mysql 에 테스트 사용됨
public class RefreshTokenRepositoryTest {
	
	@Autowired
	RefreshTokenRepository repository;
	
	private final String refreshToken = "refreshToken";
	private final String keyEmail = "keyEmail";
	
	@Test
	@DisplayName("refreshToken 탐색")
	public void searchRefreshToken() {
		// given
		RefreshToken refreshTokenInstance = refreshToken();
		
		// when
		repository.save(refreshTokenInstance);
		RefreshToken result = repository.findByRefreshToken(refreshTokenInstance.getRefreshToken()).get();
		
		// then
		assertThat(result).isNotNull();
		assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
	}
	
	@Test
	@DisplayName("refreshToken 저장")
	public void saveRefreshToken() {
		// given
		RefreshToken refreshTokenInstance = refreshToken();
		
		// when
		RefreshToken result = repository.save(refreshTokenInstance);
		
		// then
		assertThat(result).isNotNull();
		assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
	}
	
	@Test
	@DisplayName("refreshToken 삭제")
	public void removeRefreshToken() {
		// given
		RefreshToken refreshTokenInstance = refreshToken();
		
		// when
		RefreshToken result1 = repository.save(refreshTokenInstance);
		repository.deleteByKeyEmail(refreshTokenInstance.getKeyEmail());
		RefreshToken result2 = repository.existsByKeyEmail(keyEmail);
		
		// then
		assertThat(result1).isNotNull();
		assertThat(result2).isNull();
		assertThat(result1.getRefreshToken()).isEqualTo(refreshToken);
	}
	
	public RefreshToken refreshToken() {
		return RefreshToken.builder()
				.refreshToken(refreshToken)
				.keyEmail(keyEmail)
				.build();
	}
	
}
