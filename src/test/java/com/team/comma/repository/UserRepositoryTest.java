package com.team.comma.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.team.comma.entity.UserEntity;
import com.team.comma.entity.UserEntity.UserType;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // EmbeddedDatabase 가 아닌 Mysql 에 테스트 사용됨
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	private String userEmail = "email@naver.com";
	private String userPassword = "password";

	@Test
	@DisplayName("사용자 등록")
	public void registUser() {
		// given
		UserEntity userEntity = getUserEntity();

		// when
		UserEntity result = userRepository.save(userEntity);

		// then
		assertThat(result.getEmail()).isEqualTo(userEmail);
		assertThat(result.getPassword()).isEqualTo(userPassword);
	}

	@Test
	@DisplayName("사용자 탐색")
	public void findUser() {
		// given
		UserEntity userEntity = getUserEntity();

		// when
		userRepository.save(userEntity);
		UserEntity result = userRepository.findByEmail(userEntity.getEmail());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo(userEmail);
	}

	private UserEntity getUserEntity() {
		return UserEntity.builder().email(userEmail).password(userPassword).userType(UserType.GeneralUser)
				.roles(Collections.singletonList("ROLE_USER")).build();
	}

}
