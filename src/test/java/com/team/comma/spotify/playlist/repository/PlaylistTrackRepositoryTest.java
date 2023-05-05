package com.team.comma.spotify.playlist.repository;


import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.repository.TrackRepository;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
    public void 플레이리스트_곡조회_실패_데이터없음() {
        // given
        userRepository.save(getUser());
        final User user = userRepository.findByEmail(userEmail);

        Playlist playlist = playlistRepository.save(getPlaylist(user, "테스트 플레이리스트"));

        // when
        final List<PlaylistTrack> result = playlistTrackRepository.findAllByPlaylist_Id(
            playlist.getId());

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 플레이리스트_곡조회_성공_2() {
        // given
        userRepository.save(getUser());
        final User user = userRepository.findByEmail(userEmail);

        final Playlist playlist = playlistRepository.save(getPlaylist(user, "테스트 플레이리스트"));

        final Track track1 = trackRepository.save(getTrack("track1"));
        final Track track2 = trackRepository.save(getTrack("track2"));
        playlistTrackRepository.save(getPlaylistTrack(playlist, track1));
        playlistTrackRepository.save(getPlaylistTrack(playlist, track2));

        // when
        final List<PlaylistTrack> result = playlistTrackRepository.findAllByPlaylist_Id(
            playlist.getId());

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void 플리_트랙으로_TrackPlaylist_성공() {
        //given
        Track track = Track.builder().build();
        trackRepository.save(track);

        Playlist playlist = Playlist.builder().build();
        playlistRepository.save(playlist);

        PlaylistTrack playlistTrack = PlaylistTrack.builder()
            .playlist(playlist)
            .track(track)
            .build();
        playlistTrackRepository.save(playlistTrack);

        //when
        boolean isPresent = playlistTrackRepository
            .findByTrackIdAndPlaylistId(track.getId(), playlist.getId())
            .isPresent();

        //then
        assertThat(isPresent).isTrue();
    }

    @Test
    void 플리_id_트랙_id로_삭제_성공() {
        //given
        Track track = Track.builder().build();
        trackRepository.save(track);

        Playlist playlist = Playlist.builder().build();
        playlistRepository.save(playlist);

        PlaylistTrack playlistTrack = PlaylistTrack.builder()
            .playlist(playlist)
            .track(track)
            .build();
        playlistTrackRepository.save(playlistTrack);
        //when
        int deleteCount = playlistTrackRepository.deletePlaylistTrackByTrackIdAndPlaylistId(
            track.getId(),
            playlist.getId());

        Optional<PlaylistTrack> deletePlaylistTrack =
            playlistTrackRepository.findByTrackIdAndPlaylistId(track.getId(), playlist.getId());
        //then
        assertThat(deleteCount).isEqualTo(1);

        assertThat(deletePlaylistTrack).isEmpty();
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

    private Track getTrack(String title) {
        return Track.builder()
            .trackTitle(title)
            .build();
    }

    private PlaylistTrack getPlaylistTrack(Playlist playlist, Track track) {
        return PlaylistTrack.builder()
            .playlist(playlist)
            .track(track)
            .build();
    }
}
