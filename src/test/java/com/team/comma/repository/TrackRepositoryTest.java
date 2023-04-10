package com.team.comma.repository;
import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.Playlist;
import com.team.comma.domain.Track;
import com.team.comma.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;  //자동 import되지 않음

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TrackRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackRepository trackRepository;

    final String userEmail = "email@naver.com";

    @Test
    public void 곡정보조회_0(){
        // given

        // when
        List<Track> result = trackRepository.findAllByPlaylistId(1234L);

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 곡정보조회_2(){
        // given
        userRepository.save(getUser());
        User user = userRepository.findByEmail(userEmail);
        playlistRepository.save(getPlaylist(user, "테스트 플레이리스트"));
        List<Playlist> playlist = playlistRepository.findAllByUser_Email(userEmail);
        trackRepository.save(getTrack(playlist.get(0),"track1"));
        trackRepository.save(getTrack(playlist.get(0),"track2"));

        // when
        List<Track> result = trackRepository.findAllByPlaylistId(playlist.get(0).getId());

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    private User getUser() {
        return User.builder()
                .email(userEmail)
                .type(UserType.GeneralUser)
                .role(UserRole.USER)
                .build();
    }

    private Playlist getPlaylist(User user, String title) {
        return Playlist.builder()
                .playlistTitle(title)
                .user(user)
                .build();
    }

    private Track getTrack(Playlist playlist, String title) {
        return Track.builder()
                .trackTitle(title)
                .playlist(playlist)
                .build();
    }
}
