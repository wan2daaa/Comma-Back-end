package com.team.comma.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.team.comma.user.domain.QFavoriteArtist.favoriteArtist;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class FavoriteArtistRepositoryTest {
    @Autowired
    FavoriteArtistRepository favoriteArtistRepository;

    @Autowired
    UserRepository userRepository;

    private String userEmail = "email@naver.com";
    private String userPassword = "password";

    @Autowired
    private JPAQueryFactory queryFactory;

    @Test
    @DisplayName("사용자 관심 아티스트 가져오기")
    public void getArtistGenreRepository() {
        // given
        User user = getUserEntity();
        userRepository.save(user);
        user.addFavoriteArtist("artist1");
        user.addFavoriteArtist("artist2");
        user.addFavoriteArtist("artist3");

        // when
        List<String> result = queryFactory.select(favoriteArtist.artistName)
                .from(favoriteArtist)
                .where(favoriteArtist.user.eq(user))
                .fetch();

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    private User getUserEntity() {
        return User.builder().email(userEmail).password(userPassword).type(UserType.GENERAL_USER)
                .role(UserRole.USER).build();
    }
}
