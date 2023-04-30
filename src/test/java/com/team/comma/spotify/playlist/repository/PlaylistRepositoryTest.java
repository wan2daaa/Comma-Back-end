package com.team.comma.spotify.playlist.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlaylistRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    final String userEmail = "email@naver.com";

    @Test
    void 플레이리스트조회_실패_데이터없음() {
        // given

        // when
        final List<Playlist> result = playlistRepository.findAllByUser_Email(userEmail);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 플레이리스트조회_성공_2() {
        // given
        userRepository.save(getUser());
        final User user = userRepository.findByEmail(userEmail);

        playlistRepository.save(getPlaylist(user, "테스트 플레이리스트1"));
        playlistRepository.save(getPlaylist(user, "테스트 플레이리스트2"));

        // when
        final List<Playlist> result = playlistRepository.findAllByUser_Email(userEmail);

        // then
        assertThat(result).hasSize(2);
    }


    private User getUser() {
        return User.builder()
            .email(userEmail)
            .type(UserType.GENERAL_USER)
            .role(UserRole.USER)
            .build();
    }

    private Playlist getPlaylist(User user, String title) {
        return Playlist.builder()
            .playlistTitle(title)
            .alarmFlag(true)
            .user(user)
            .build();
    }

}
