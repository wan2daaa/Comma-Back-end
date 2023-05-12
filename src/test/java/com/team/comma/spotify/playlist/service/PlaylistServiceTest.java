package com.team.comma.spotify.playlist.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.Exception.PlaylistException;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackArtist;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {
    @InjectMocks
    private PlaylistService playlistService;
    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private String userEmail = "email@naver.com";
    private long playlistId = 123L;
    private boolean flag = false;
    private String token = "accessToken";

    @Test
    public void 플레이리스트_조회() throws AccountException {
        // given
        final User user = User.builder()
                .email(userEmail)
                .type(UserType.GENERAL_USER)
                .role(UserRole.USER)
                .build();

        Optional<User> optionalUser = Optional.of(user);
        doReturn(optionalUser).when(userRepository).findByEmail(user.getEmail());
        doReturn(userEmail).when(jwtTokenProvider).getUserPk(token);

        final List<TrackArtist> artistList = Arrays.asList(
                TrackArtist.builder().id(123L).build()
        );

        final Track track = Track.builder()
                .id(123L)
                .trackArtistList(artistList)
                .build();

        final List<PlaylistTrack> playlistTrack = Arrays.asList(
                PlaylistTrack.builder().track(track).trackAlarmFlag(true).build()
        );

        doReturn(Arrays.asList(
                Playlist.builder().id(1L).alarmFlag(true).playlistTrackList(playlistTrack).build(),
                Playlist.builder().id(2L).alarmFlag(true).playlistTrackList(playlistTrack).build(),
                Playlist.builder().id(3L).alarmFlag(true).playlistTrackList(playlistTrack).build()
        )).when(playlistRepository).findAllByUser(user);

        // when
        final List<PlaylistResponse> result = playlistService.getPlaylists(token);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 플레이리스트_알림설정변경_실패_존재하지않는플레이리스트() {
        // given

        // when
        final PlaylistException result = assertThrows(PlaylistException.class, () -> playlistService.updateAlarmFlag(playlistId, flag));

        // then
        assertThat(result.getMessage()).isEqualTo("알람 설정 변경에 실패했습니다. 플레이리스트를 찾을 수 없습니다.");
    }

    @Test
    public void 플레이리스트_알림설정변경_성공() {
        // given
        doReturn(Optional.of(Playlist.builder()
                .id(playlistId)
                .alarmFlag(false)
                .build()
        )).when(playlistRepository).findById(playlistId);

        // when
        final MessageResponse result = playlistService.updateAlarmFlag(playlistId,flag);

        // then
        assertThat(result.getCode()).isEqualTo(2);
        assertThat(result.getMessage()).isEqualTo("알람 설정이 변경되었습니다.");
    }

}
