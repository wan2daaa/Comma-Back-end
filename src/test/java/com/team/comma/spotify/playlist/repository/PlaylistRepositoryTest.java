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

    private final String userEmail = "email@naver.com";
    private final String title = "test playlist";

    @Test
    public void 플레이리스트_저장(){
        // given
        final User user = userRepository.save(getUser());

        // when
        final Playlist result = playlistRepository.save(getPlaylist(user, title));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlaylistTitle()).isEqualTo(title);
    }

    @Test
    public void 플레이리스트_조회_0개(){
        // given
        final User user = userRepository.save(getUser());

        // when
        final List<Playlist> result = playlistRepository.findAllByUser(user);

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 플레이리스트_조회_2개(){
        // given
        final User user = userRepository.save(getUser());
        playlistRepository.save(getPlaylist(user, title));
        playlistRepository.save(getPlaylist(user, title));

        // when
        final List<Playlist> result = playlistRepository.findAllByUser(user);

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void 플레이리스트_알람설정변경(){
        // given
        final User user = userRepository.save(getUser());
        final Playlist playlist = playlistRepository.save(getPlaylist(user, "test playlist"));

        // when
        int result = playlistRepository.updateAlarmFlag(playlist.getId(),false);

        // then
        assertThat(result).isEqualTo(1);
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
