package com.team.comma.spotify.playlist.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistUpdateRequest;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.repository.TrackRepository;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.config.TestConfig;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestConfig.class)
class PlaylistRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistTrackRepository playlistTrackRepository;

    @Autowired
    private TrackRepository trackRepository;

    private final String userEmail = "email@naver.com";
    private final String title = "test playlist";

    @Test
    void 플레이리스트_저장() {
        // given
        final User user = userRepository.save(buildUser());

        // when
        final Playlist result = playlistRepository.save(buildPlaylist(user, title));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPlaylistTitle()).isEqualTo(title);
    }

    @Test
    void 플레이리스트_조회_0개() {
        // given
        final User user = userRepository.save(buildUser());

        // when
        final List<Playlist> result = playlistRepository.findAllByUserAndDelFlag(user, false);

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void 플레이리스트_조회_2개() {
        // given
        final User user = userRepository.save(buildUser());
        playlistRepository.save(buildPlaylist(user, title));
        playlistRepository.save(buildPlaylist(user, title));

        // when
        final List<Playlist> result = playlistRepository.findAllByUserAndDelFlag(user, false);

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void 플레이리스트_알람설정변경() {
        // given
        final User user = userRepository.save(buildUser());
        final Playlist playlist = playlistRepository.save(buildPlaylist(user, "test playlist"));

        // when
        long result = playlistRepository.updateAlarmFlag(playlist.getId(), false);

        // then
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 플레이리스트_삭제(){
        // given
        final User user = userRepository.save(buildUser());
        final Playlist playlist = playlistRepository.save(buildPlaylist(user, "test playlist"));

        // when
        long result = playlistRepository.deletePlaylist(playlist.getId());

        // then
        assertThat(result).isEqualTo(1);
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
    void 플리가_존재하지않으면_플리_listSequence_0_리턴() {
        //given
        //when
        int maxListSequence = playlistRepository.findMaxListSequence();

        //then
        assertThat(maxListSequence).isZero();
    }

    @Test
    void 플리를_PlaylistRequest_로_저장한다() {
        //given
        User user = User.builder().email("test@email.com").build();
        userRepository.save(user);

        PlaylistUpdateRequest playlistUpdateRequest = PlaylistUpdateRequest.builder()
            .playlistTitle("플리제목")
            .alarmStartTime(LocalTime.now())
            .user(user)
            .listSequence(1)
            .build();

        Playlist playlist = playlistUpdateRequest.toEntity();
        //when
        Playlist savedPlaylist = playlistRepository.save(playlist);

        //then
        assertThat(savedPlaylist.getPlaylistTitle()).isEqualTo(playlist.getPlaylistTitle());
        assertThat(savedPlaylist.getAlarmStartTime()).isEqualTo(playlist.getAlarmStartTime());
        assertThat(savedPlaylist.getUser()).isEqualTo(playlist.getUser());
        assertThat(savedPlaylist.getListSequence()).isEqualTo(playlist.getListSequence());

    }

    @Test
    void 플리의_내용을_수정한다() {
        //given
        Playlist playlist = buildPlaylist();
        playlistRepository.save(playlist);

        PlaylistUpdateRequest playlistUpdateRequest = PlaylistUpdateRequest.builder()
            .id(playlist.getId())
            .playlistTitle("플리제목변경")
            .alarmStartTime(LocalTime.now())
            .listSequence(2)
            .build();

        //when
        playlist.updatePlaylist(playlistUpdateRequest);

        Playlist updatedPlaylist = playlistRepository.findById(playlistUpdateRequest.getId()).get();

        //then
        assertThat(updatedPlaylist.getPlaylistTitle()).isEqualTo(
            playlistUpdateRequest.getPlaylistTitle());
        assertThat(updatedPlaylist.getListSequence()).isEqualTo(
            playlistUpdateRequest.getListSequence());
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

    private User buildGeneralUser() {
        return User.builder()
            .email(userEmail)
            .type(UserType.GENERAL_USER)
            .role(UserRole.USER)
            .build();
    }

    private Playlist buildPlaylist(User user, String title, List<Track> trackList) {
        return Playlist.builder()
            .playlistTitle(title)
            .alarmFlag(true)
            .user(user)
            .build();
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


}
