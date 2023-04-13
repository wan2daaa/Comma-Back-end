package com.team.comma.repository;

import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.Playlist;
import com.team.comma.domain.PlaylistTrack;
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
public class PlaylistTrackRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;

    final String userEmail = "email@naver.com";
    @Test
    public void 플레이리스트_곡조회_실패_데이터없음(){
        // given
        userRepository.save(getUser());
        final User user = userRepository.findByEmail(userEmail);

        Playlist playlist = playlistRepository.save(getPlaylist(user, "테스트 플레이리스트"));

        // when
        final List<PlaylistTrack> result = playlistTrackRepository.findAllByPlaylist_Id(playlist.getId());

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 플레이리스트_곡조회_성공_2(){
        // given
        userRepository.save(getUser());
        final User user = userRepository.findByEmail(userEmail);

        final Playlist playlist = playlistRepository.save(getPlaylist(user, "테스트 플레이리스트"));

        final Track track1 = trackRepository.save(getTrack("track1"));
        final Track track2 = trackRepository.save(getTrack("track2"));
        playlistTrackRepository.save(getPlaylistTrack(playlist,track1));
        playlistTrackRepository.save(getPlaylistTrack(playlist,track2));

        // when
        final List<PlaylistTrack> result = playlistTrackRepository.findAllByPlaylist_Id(playlist.getId());

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

    private Track getTrack(String title) {
        return Track.builder()
                .trackTitle(title)
                .build();
    }

    private PlaylistTrack getPlaylistTrack(Playlist playlist,Track track) {
        return PlaylistTrack.builder()
                .playlist(playlist)
                .track(track)
                .build();
    }
}
