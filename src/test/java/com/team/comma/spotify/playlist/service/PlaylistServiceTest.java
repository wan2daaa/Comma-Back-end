package com.team.comma.spotify.playlist.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistUpdateRequest;
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
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

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
    void 플레이리스트_조회() throws AccountException {
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
        assertThat(result).hasSize(3);
    }

    @Test
    void 플레이리스트_알림설정변경_성공() {
        // given

        // when
        final MessageResponse result = playlistService.updateAlarmFlag(playlistId, flag);

        // then
        assertThat(result.getCode()).isEqualTo(2);
        assertThat(result.getMessage()).isEqualTo("알람 설정이 변경되었습니다.");
    }

    @Test
    void 플레이리스트의_총재생시간을_리턴한다() {
        //given
        final long PLAYLIST_ID = 1L;
        final int TOTAL_DURATION_TIME = 100;

        doReturn(TOTAL_DURATION_TIME)
            .when(playlistRepository).getTotalDurationTimeMsWithPlaylistId(PLAYLIST_ID);

        //when
        MessageResponse<Integer> totalDurationTimeMsDto = playlistService.getTotalDurationTimeMsByPlaylist(
            PLAYLIST_ID);

        //then
        assertThat(totalDurationTimeMsDto.getData()).isEqualTo(TOTAL_DURATION_TIME);
    }

    @Test
    void 플리를_PlaylistRequest로_수정하고_MessageResponse를_반환한다() {
        //given
        PlaylistUpdateRequest playlistRequest = PlaylistUpdateRequest.builder()
            .id(1L)
            .playlistTitle("플리제목수정")
            .alarmStartTime(LocalTime.now())
            .listSequence(2)
            .build();

        doReturn(Optional.of(buildPlaylist()))
            .when(playlistRepository).findById(playlistRequest.getId());

        //when
        MessageResponse messageResponse = playlistService.updatePlaylist(playlistRequest);

        //then
        assertThat(messageResponse.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(messageResponse.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    private Playlist buildPlaylist() {
        return Playlist.builder()
            .user(buildUserWithEmail())
            .build();
    }

    private User buildUserWithEmail() {
        return User.builder().email(userEmail).build();
    }

}
