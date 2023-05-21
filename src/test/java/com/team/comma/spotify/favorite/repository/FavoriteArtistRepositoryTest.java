package com.team.comma.spotify.favorite.repository;

import com.team.comma.spotify.favorite.artist.domain.FavoriteArtist;
import com.team.comma.spotify.favorite.artist.repository.FavoriteArtistRepository;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FavoriteArtistRepositoryTest {
    @Autowired
    FavoriteArtistRepository favoriteArtistRepository;

    @Autowired
    UserRepository userRepository;

    private String userEmail = "email@naver.com";
    private String userPassword = "password";

    @Test
    @DisplayName("관심 아티스트 추가")
    public void addFavoriteArtist() {
        // given
        User user = User.builder().build();
        FavoriteArtist favoriteArtist = FavoriteArtist.builder().artistImageUrl("URL").artistName("name").user(user).build();

        // when
        FavoriteArtist result = favoriteArtistRepository.save(favoriteArtist);

        // then
        assertThat(result.getArtistName()).isEqualTo("name");
        assertThat(result.getArtistImageUrl()).isEqualTo("URL");
    }

    @Test
    @DisplayName("사용자 관심 아티스트 삭제")
    public void deleteFavoriteArtist() {
        // given
        User userEntity = User.builder().email("email").build();
        userRepository.save(userEntity);
        userEntity.addFavoriteArtist("artist");

        // when
        favoriteArtistRepository.deleteByUser(userEntity , "artist");

        // then
        FavoriteArtist result = favoriteArtistRepository.findFavoriteArtistByUser(userEntity , "artist").orElse(null);

        assertThat(result).isNull();

    }

    @Test
    @DisplayName("사용자 관심 아티스트 가져오기")
    public void getFavoriteArtistRepository() {
        // given
        User user = getUserEntity();
        userRepository.save(user);
        user.addFavoriteArtist("artist1");
        user.addFavoriteArtist("artist2");
        user.addFavoriteArtist("artist3");

        // when
        List<String> result = favoriteArtistRepository.findFavoriteArtistListByUser(user);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("유저가 추가한 하나의 아티스트 가져오기")
    public void getFavoriteArtistByUser() {
        // given
        User user = getUserEntity();
        userRepository.save(user);
        user.addFavoriteArtist("artist1");

        // when
        FavoriteArtist result = favoriteArtistRepository.findFavoriteArtistByUser(user , "artist1").orElse(null);

        // then
        assertThat(result).isNotNull();
    }

    private User getUserEntity() {
        return User.builder().email(userEmail).password(userPassword).type(UserType.GENERAL_USER)
                .role(UserRole.USER).build();
    }

}
