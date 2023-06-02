package com.team.comma.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import com.team.comma.util.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.team.comma.user.domain.QFavoriteGenre.favoriteGenre;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FavoriteGenreRepositoryTest {

    @Autowired
    FavoriteGenreRepository interestGenreRepository;

    @Autowired
    UserRepository userRepository;

    private String userEmail = "email@naver.com";
    private String userPassword = "password";

    @Autowired
    private JPAQueryFactory queryFactory;

    @Test
    @DisplayName("사용자 관심 장르 가져오기")
    public void getInterestGenreRepository() {
        // given
        User user = getUserEntity();
        userRepository.save(user);
        user.addFavoriteGenre("genre1");
        user.addFavoriteGenre("genre2");
        user.addFavoriteGenre("genre3");

        // when
        List<String> result = queryFactory.select(favoriteGenre.genreName)
                .from(favoriteGenre)
                .where(favoriteGenre.user.eq(user))
                .fetch();

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    private User getUserEntity() {
        return User.builder().email(userEmail).password(userPassword).type(UserType.GENERAL_USER)
                .role(UserRole.USER).build();
    }
}
