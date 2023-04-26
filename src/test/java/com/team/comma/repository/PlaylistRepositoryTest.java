package com.team.comma.repository;

import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.Playlist;
import com.team.comma.domain.PlaylistTrack;
import com.team.comma.domain.Track;
import com.team.comma.domain.User;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;  //자동 import되지 않음

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
        assertThat(result.size()).isEqualTo(0);
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
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void 플레이리스트별_트랙의총재생시간() {
//        //given
//        Track track100 = buildTrackWithDurationTimeMs(100);
//        Track track200 = buildTrackWithDurationTimeMs(100);
//
//        trackRepository.save(track100);
//        trackRepository.save(track200);
//
//        Playlist playlist = Playlist.builder()
//            .playlistTitle("테스트 플레이리스트")
//            .alarmFlag(true)
//            .user(getGeneralUser())
//            .build();
//
//        playlistRepository.save(playlist);
//
//        PlaylistTrack playlistTrack1 = PlaylistTrack.builder()
//            .track(track100)
//            .playlist(playlist)
//            .build();
//
//        PlaylistTrack playlistTrack2 = PlaylistTrack.builder()
//            .track(track200)
//            .playlist(playlist)
//            .build();
//
//        playlistTrackRepository.save(playlistTrack1);
//        playlistTrackRepository.save(playlistTrack2);

        // given
//        Playlist playlist = Playlist.builder()
//            .playlistTitle("My Playlist")
//            .alarmFlag(true)
//            .alarmStartTime(LocalTime.of(6, 0))
//            .listSequence(1)
//            .build();
//        playlistRepository.save(playlist);
//
//        Track track1 = Track.builder()
//            .trackTitle("Track 1")
//            .durationTimeMs(1000)
//            .build();
//        trackRepository.save(track1);
//
//        Track track2 = Track.builder()
//            .trackTitle("Track 2")
//            .durationTimeMs(2000)
//            .build();
//        trackRepository.save(track2);
//
//        PlaylistTrack playlistTrack1 = PlaylistTrack.builder()
//            .playlist(playlist)
//            .track(track1)
//            .build();
//        playlistTrackRepository.save(playlistTrack1);
//
//        PlaylistTrack playlistTrack2 = PlaylistTrack.builder()
//            .playlist(playlist)
//            .track(track2)
//            .build();
//        playlistTrackRepository.save(playlistTrack2);

        Playlist playlist = Playlist.builder()
            .playlistTitle("My Playlist")
            .alarmFlag(false)
            .build();
        playlistRepository.save(playlist);

        Track track1 = Track.builder()
            .trackTitle("Track 1")
            .durationTimeMs(100000)
            .build();
        Track track2 = Track.builder()
            .trackTitle("Track 2")
            .durationTimeMs(200000)
            .build();
        trackRepository.save(track1);
        trackRepository.save(track2);

        PlaylistTrack playlistTrack1 = PlaylistTrack.builder()
            .playlist(playlist)
            .track(track1)
            .build();
        PlaylistTrack playlistTrack2 = PlaylistTrack.builder()
            .playlist(playlist)
            .track(track2)
            .build();
        playlistTrackRepository.save(playlistTrack1);
        playlistTrackRepository.save(playlistTrack2);

        playlist.addPlaylistTrack(track1);
        playlist.addPlaylistTrack(track2);
        playlistRepository.save(playlist);

        // when
        Long durationSum = playlistRepository.getDurationSumByPlaylistId(playlist.getId());

        // then
        assertThat(durationSum).isEqualTo(300000L);
    }

    private static Track buildTrackWithDurationTimeMs(int durationTimeMs) {
        return Track.builder().durationTimeMs(durationTimeMs).build();
    }

    private User getGeneralUser() {
        return User.builder()
            .email(userEmail)
            .type(UserType.GeneralUser)
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
