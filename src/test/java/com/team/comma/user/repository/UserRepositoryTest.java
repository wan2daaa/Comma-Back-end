package com.team.comma.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import com.team.comma.user.domain.UserDetail;
import com.team.comma.util.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.team.comma.user.domain.QUser.user;
import static com.team.comma.user.domain.QUserDetail.userDetail;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// EmbeddedDatabase 가 아닌 Mysql 에 테스트 사용됨
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

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
        User result = queryFactory.select(user)
                .from(user)
                .leftJoin(user.userDetail)
                .fetchJoin()
                .where(user.email.eq(userEntity.getEmail()))
                .fetchFirst();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("이름과 닉네임으로 연관된 사용자 탐색")
    public void findUserByNickNameAndName() {
        // given
        User userEntity1 = getUserEntity();
        userEntity1.setUserDetail(UserDetail.builder().name("a01").nickname("b02").build());
        User userEntity2 = getUserEntity();
        userEntity2.setUserDetail(UserDetail.builder().name("b01").nickname("a02").build());
        User userEntity3 = getUserEntity();
        userEntity3.setUserDetail(UserDetail.builder().name("c01").nickname("c02").build());

        userRepository.save(userEntity1);
        userRepository.save(userEntity2);
        userRepository.save(userEntity3);

        // when
        List<User> result = queryFactory.select(user).from(user).join(user.userDetail).fetchJoin()
                .where(userDetail.name.eq("b01").or(userDetail.nickname.eq("c02")))
                .fetch();

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    private User getUserEntity() {
        return User.builder().email(userEmail).password(userPassword).type(UserType.GENERAL_USER)
            .role(UserRole.USER).build();
    }

}
