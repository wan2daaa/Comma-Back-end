package com.team.comma.spotify.playlist.repository;


import static org.assertj.core.api.Assertions.assertThat;

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
import com.team.comma.util.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlaylistTrackRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;

    private final String userEmail = "email@naver.com";

    private final String title = "test playlist";

    private final String trackTitle = "test track";

    @Test
    void 플레이리스트_곡_연관관계_저장() {
        // given
        final User user = userRepository.save(buildUser());
        final Playlist playlist = playlistRepository.save(buildPlaylist(user, title));
        final Track track = trackRepository.save(buildTrack(trackTitle));

        // when
        final PlaylistTrack result = playlistTrackRepository.save(
                buildPlaylistTrack(playlist, track));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlaylist().getId()).isEqualTo(playlist.getId());
        assertThat(result.getTrack().getId()).isEqualTo(track.getId());
    }

    @Test
    void 플레이리스트_곡_연관관계_조회_0개() {
        // given
        final User user = userRepository.save(buildUser());
        final Playlist playlist = playlistRepository.save(buildPlaylist(user, title));

        // when
        final List<PlaylistTrack> result = playlistTrackRepository.findAllByPlaylist(playlist);

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void 플레이리스트_곡_연관관계_조회_2개() {
        // given
        final User user = userRepository.save(buildUser());
        final Playlist playlist = playlistRepository.save(buildPlaylist(user, title));
        final Track track1 = trackRepository.save(buildTrack(trackTitle));
        final Track track2 = trackRepository.save(buildTrack(trackTitle));
        playlistTrackRepository.save(buildPlaylistTrack(playlist, track1));
        playlistTrackRepository.save(buildPlaylistTrack(playlist, track2));

        // when
        final List<PlaylistTrack> result = playlistTrackRepository.findAllByPlaylist(playlist);

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

    @Test
    void 트랙들간의_순서중_제일_높은_값을_리턴한다() {
        //given
        Track track1 = Track.builder().build();
        trackRepository.save(track1);

        Track track2 = Track.builder().build();
        trackRepository.save(track2);

        Track track3 = Track.builder().build();
        trackRepository.save(track3);

        Playlist playlist = Playlist.builder().build();
        playlistRepository.save(playlist);

        PlaylistTrack playlistTrack1 = PlaylistTrack.builder()
            .playlist(playlist)
            .track(track1)
            .playSequence(1)
            .build();
        playlistTrackRepository.save(playlistTrack1);

        PlaylistTrack playlistTrack2 = PlaylistTrack.builder()
            .playlist(playlist)
            .track(track2)
            .playSequence(2)
            .build();
        playlistTrackRepository.save(playlistTrack2);

        PlaylistTrack playlistTrack3 = PlaylistTrack.builder()
            .playlist(playlist)
            .track(track3)
            .playSequence(3)
            .build();
        playlistTrackRepository.save(playlistTrack3);

        //when
        Integer maxPlaySequence = playlistTrackRepository.findMaxPlaySequenceByPlaylistId(
            playlist.getId()).get();
        //then
        assertThat(maxPlaySequence).isEqualTo(3);
    }


    private User buildUser() {
        return User.builder()
            .email(userEmail)
            .type(UserType.GENERAL_USER)
            .role(UserRole.USER)
            .build();
    }

    private Playlist buildPlaylist(User user, String title) {
        return Playlist.builder()
            .playlistTitle(title)
            .alarmFlag(true)
            .user(user)
            .build();
    }

    private Track buildTrack(String title) {
        return Track.builder()
            .trackTitle(title)
            .build();
    }

    private PlaylistTrack buildPlaylistTrack(Playlist playlist, Track track) {
        return PlaylistTrack.builder()
            .playlist(playlist)
            .track(track)
            .build();
    }
}
