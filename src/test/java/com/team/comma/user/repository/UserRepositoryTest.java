package com.team.comma.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// EmbeddedDatabase 가 아닌 Mysql 에 테스트 사용됨
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private String userEmail = "email@naver.com";
    private String userPassword = "password";

    @Test
    @DisplayName("사용자 등록")
    public void registUser() {
        // given
        User userEntity = getUserEntity();

        // when
        User result = userRepository.save(userEntity);

        // then
        assertThat(result.getEmail()).isEqualTo(userEmail);
        assertThat(result.getPassword()).isEqualTo(userPassword);
    }

    @Test
    @DisplayName("사용자 탐색")
    public void findUser() {
        // given
        User userEntity = getUserEntity();

        // when
        userRepository.save(userEntity);
        User result = userRepository.findByEmail(userEntity.getEmail());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(userEmail);
    }

    private User getUserEntity() {
        return User.builder().email(userEmail).password(userPassword).type(UserType.GENERAL_USER)
            .role(UserRole.USER).build();
    }

}
