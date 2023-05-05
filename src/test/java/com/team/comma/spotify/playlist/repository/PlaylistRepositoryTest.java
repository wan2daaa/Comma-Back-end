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
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlaylistRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;

    @Autowired
    private TrackRepository trackRepository;

    private final String userEmail = "email@naver.com";

    @Test
    public void 플레이리스트조회_실패_데이터없음() {
        // given

        // when
        final List<Playlist> result = playlistRepository.findAllByUser_Email(userEmail);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void 플레이리스트조회_성공_2() {
        // given
        userRepository.save(getGeneralUser());
        final User user = userRepository.findByEmail(userEmail);

        playlistRepository.save(getPlaylist(user, "테스트 플레이리스트1", List.of()));
        playlistRepository.save(getPlaylist(user, "테스트 플레이리스트2", List.of()));

        // when
        final List<Playlist> result = playlistRepository.findAllByUser_Email(userEmail);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void 특정_플리에_트랙이_없다면_플리의_총재생시간은_0으로_리턴() {
        //given
        Playlist playlist = buildPlaylist();
        playlistRepository.save(playlist);

        //when
        int durationTimeSum = playlistRepository.getTotalDurationTimeMsWithPlaylistId(
            playlist.getId());

        //then
        assertThat(durationTimeSum).isEqualTo(0);
    }

    @Test
    void 하나의_플리의_총재생시간을_리턴() {
        // given
        Playlist playlist = buildPlaylist();
        playlistRepository.save(playlist);

        Track track1 = buildTrackWithDurationTimeMs(1000);
        Track track2 = buildTrackWithDurationTimeMs(2000);
        trackRepository.save(track1);
        trackRepository.save(track2);

        PlaylistTrack playlistTrack1 = buildPlaylistTrackWithPlaylistAndTrack(playlist, track1);
        PlaylistTrack playlistTrack2 = buildPlaylistTrackWithPlaylistAndTrack(playlist, track2);
        playlistTrackRepository.save(playlistTrack1);
        playlistTrackRepository.save(playlistTrack2);

        playlist.addPlaylistTrack(track1);
        playlist.addPlaylistTrack(track2);
        playlistRepository.save(playlist);
        // when
        int durationSum = playlistRepository.getTotalDurationTimeMsWithPlaylistId(playlist.getId());

        // then
        assertThat(durationSum).isEqualTo(3000L);
    }

    @Test
    void 플리_저장_성공() {
        //given
        Playlist playlist = buildPlaylist();
        //when
        Playlist savedPlaylist = playlistRepository.save(playlist);
        //then
        assertThat(playlist).isEqualTo(savedPlaylist);
    }

    @Test
    void 플리간_순서중_제일_큰값을_리턴한다() {
        //given
        Playlist playlist1 = buildPlaylistWithListSequence(1);
        playlistRepository.save(playlist1);

        Playlist playlist2 = buildPlaylistWithListSequence(2);
        playlistRepository.save(playlist2);

        //when
        int maxListSequence = playlistRepository.findMaxListSequence();
        //then
        assertThat(maxListSequence).isEqualTo(2);
    }

    @Test
    void 플리가_존재하지않으면_플리간_순서_0_리턴() {
        //given
        //when
        int maxListSequence = playlistRepository.findMaxListSequence();

        //then
        assertThat(maxListSequence).isZero();
    }


    private Playlist buildPlaylistWithListSequence(int listSequence) {
        return Playlist.builder()
            .listSequence(listSequence)
            .build();
    }

    private PlaylistTrack buildPlaylistTrackWithPlaylistAndTrack(Playlist playlist,
        Track track1) {
        PlaylistTrack playlistTrack1 = PlaylistTrack.builder()
            .playlist(playlist)
            .track(track1)
            .build();
        return playlistTrack1;
    }

    private Playlist buildPlaylist() {
        Playlist playlist = Playlist.builder()
            .playlistTitle("My Playlist")
            .alarmFlag(false)
            .build();
        return playlist;
    }

    private Track buildTrackWithDurationTimeMs(int durationTimeMs) {
        return Track.builder().durationTimeMs(durationTimeMs).build();
    }

    private User getGeneralUser() {
        return User.builder()
            .email(userEmail)
            .type(UserType.GENERAL_USER)
            .role(UserRole.USER)
            .build();
    }

    private Playlist getPlaylist(User user, String title, List<Track> trackList) {
        return Playlist.builder()
            .playlistTitle(title)
            .alarmFlag(true)
            .user(user)
            .build();
    }


}
