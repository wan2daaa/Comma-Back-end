package com.team.comma.spotify.playlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistRequest;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.security.auth.login.AccountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @InjectMocks
    private PlaylistService playlistService;
    @Mock
    private PlaylistTrackRepository playlistTrackRepository;
    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserRepository userRepository;

    private String userEmail = "email@naver.com";

    @Test
    void 플레이리스트_조회_성공() {
        // given
        doReturn(Arrays.asList(
            Playlist.builder().build(),
            Playlist.builder().build(),
            Playlist.builder().build()
        )).when(playlistRepository).findAllByUser_Email(userEmail);

        // when
        final List<Playlist> result = playlistService.getPlaylist(userEmail);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    void 플레이리스트_곡조회_성공() {
        // given
        doReturn(Arrays.asList(
            PlaylistTrack.builder().build(),
            PlaylistTrack.builder().build(),
            PlaylistTrack.builder().build()
        )).when(playlistTrackRepository).findAllByPlaylist_Id(123L);

        // when
        final List<PlaylistTrack> result = playlistService.getPlaylistTrack(123L);

        // then
        assertThat(result).hasSize(3);
    }

    @Test
    void 사용자플레이리스트_조회_성공() {
        // given
        doReturn(Arrays.asList(
            Playlist.builder().build(),
            Playlist.builder().build(),
            Playlist.builder().build()
        )).when(playlistTrackRepository).findAllByPlaylist_Id(123L);

        // when
        final List<PlaylistTrack> result = playlistService.getPlaylistTrack(123L);

        // then
        assertThat(result).hasSize(3);
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
    void 플리를_저장하고_MessageResponse를_리턴한다() throws AccountException {
        //given
        String accessToken = "accessToken";

        PlaylistRequest playlistRequest = PlaylistRequest.builder()
            .playlistTitle("플리제목")
            .alarmStartTime(LocalTime.now())
            .build();

        Playlist playlist = playlistRequest.toEntity();

        doReturn(userEmail)
            .when(jwtTokenProvider).getUserPk(accessToken);

        doReturn(User.builder().email(userEmail).build())
            .when(userRepository).findByEmail(userEmail);

        doReturn(1)
            .when(playlistRepository).findMaxListSequence();

        doReturn(playlist)
            .when(playlistRepository).save(any(Playlist.class));

        //when
        MessageResponse messageResponse = playlistService.createPlaylist(playlistRequest,
            accessToken);
        //then
        assertThat(messageResponse.getCode()).isEqualTo(1);
        assertThat(messageResponse.getMessage()).isEqualTo("요청에 성공적으로 응답하였습니다.");
    }

    @Test
    void 플리를_PlaylistRequest로_수정하고_MessageResponse를_반환한다() {
        //given
        PlaylistRequest playlistRequest = PlaylistRequest.builder()
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
        assertThat(messageResponse.getCode()).isEqualTo(1);
        assertThat(messageResponse.getMessage()).isEqualTo("요청에 성공적으로 응답하였습니다.");
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
