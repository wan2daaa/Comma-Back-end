package com.team.comma.spotify.playlist.service;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistTrackSaveRequestDto;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackArtist;
import com.team.comma.spotify.track.dto.TrackRequest;
import com.team.comma.spotify.track.repository.TrackRepository;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.security.auth.login.AccountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author: wan2daaa
 */

@ExtendWith(MockitoExtension.class)
class PlaylistTrackServiceTest {

    @InjectMocks
    PlaylistTrackService playlistTrackService;

    @Mock
    PlaylistTrackRepository playlistTrackRepository;

    @Mock
    PlaylistRepository playlistRepository;

    @Mock
    TrackRepository trackRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    UserRepository userRepository;


    private String spotifyTrackId = "input ISRC of track";

    @Test
    void 플리에_담긴_트랙들을_삭제한다() {
        //given
        final Set<Long> trackIdList = Set.of(1L, 2L, 3L);
        final long playlistId = 1L;
        final int sizeOfTrackIdList = trackIdList.size();

        doReturn(1)
            .when(playlistTrackRepository)
            .deletePlaylistTrackByTrackIdAndPlaylistId(anyLong(), anyLong());

        doReturn(Optional.of(PlaylistTrack.builder().build()))
            .when(playlistTrackRepository)
            .findByTrackIdAndPlaylistId(anyLong(), anyLong());
        //when
        int deleteCount = (int) playlistTrackService.removePlaylistAndTrack(trackIdList,
                playlistId)
            .getData();

        //then
        assertThat(deleteCount).isEqualTo(sizeOfTrackIdList);
    }

    @Test
    void 플리에_없는_트랙과의_관계를_끊을려고하면_에러_발생() {
        //given
        long playlistId = 1L;
        doThrow(EntityNotFoundException.class)
            .when(playlistTrackRepository)
            .findByTrackIdAndPlaylistId(anyLong(), anyLong());
        //when //then
        assertThrows(EntityNotFoundException.class,
            () -> {
                playlistTrackService.removePlaylistAndTrack(Set.of(1L, 2L, 3L), playlistId);
            });

    }

    @Test
    void 사용자가_존재하지않으면_UsernameNotFoundException() {
        //given
        final String accessToken = "accessToken";

        PlaylistTrackSaveRequestDto requestDto = PlaylistTrackSaveRequestDto.builder()
            .playlistTitle("플리 타이틀")
            .alarmStartTime(LocalTime.now())
            .listSequence(1)
            .trackList(List.of(TrackRequest.builder().build()))
            .build();

        //when
        //then
        assertThatThrownBy(
            () -> playlistTrackService.savePlaylistTrackList(requestDto, accessToken))
            .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void 트랙_저장_성공_playlistIdList_비어있고_track_존재하지않으면_트랙_신규_저장_후_플레이리스트_신규_저장() throws AccountException {
        //given
        final String accessToken = "accessToken";
        final String userEmail = "test@email.com";
        final User user = User.builder().email(userEmail).build();

        TrackRequest trackRequest = TrackRequest.builder()
                .trackTitle("test track")
                .albumImageUrl("url/track")
                .spotifyTrackId(spotifyTrackId)
                .spotifyTrackHref("href string")
                .trackArtistList(List.of("test artist"))
                .build();

        PlaylistTrackSaveRequestDto requestDto = PlaylistTrackSaveRequestDto.builder()
                .playlistIdList(List.of())
                .playlistTitle("플리 타이틀")
                .alarmStartTime(LocalTime.now())
                .listSequence(1)
                .trackList(List.of(trackRequest))
                .build();

        doReturn(userEmail)
                .when(jwtTokenProvider).getUserPk(accessToken);
        doReturn(Optional.of(user))
                .when(userRepository).findByEmail(userEmail);

        //when
        final MessageResponse result = playlistTrackService.savePlaylistTrackList(requestDto, accessToken);

        //then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    @Test
    void 트랙_저장_성공_playlistIdList_비어있고_track_존재하면_플레이리스트_신규_저장() throws AccountException {
        //given
        final String accessToken = "accessToken";
        final String userEmail = "test@email.com";
        final User user = User.builder().email(userEmail).build();

        TrackRequest trackRequest = TrackRequest.builder()
                .trackTitle("test track")
                .albumImageUrl("url/track")
                .spotifyTrackId(spotifyTrackId)
                .spotifyTrackHref("href string")
                .trackArtistList(List.of("test artist"))
                .build();
        Track track = trackRequest.toTrackEntity();

        PlaylistTrackSaveRequestDto requestDto = PlaylistTrackSaveRequestDto.builder()
                .playlistIdList(List.of())
                .playlistTitle("플리 타이틀")
                .alarmStartTime(LocalTime.now())
                .listSequence(1)
                .trackList(List.of(trackRequest))
                .build();

        doReturn(userEmail)
                .when(jwtTokenProvider).getUserPk(accessToken);
        doReturn(Optional.of(user))
                .when(userRepository).findByEmail(userEmail);
        doReturn(Optional.of(track))
                .when(trackRepository).findBySpotifyTrackId(trackRequest.getSpotifyTrackId());

        //when
        final MessageResponse result = playlistTrackService.savePlaylistTrackList(requestDto, accessToken);

        //then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    void 트랙_저장_성공_playlistIdList_비어있지않고_track_존재하지않으면_트랙_신규_저장_후_플레이리스트_트랙_추가() throws AccountException {
        //given
        final String accessToken = "accessToken";
        final String userEmail = "test@email.com";
        final User user = User.builder().email(userEmail).build();

        TrackRequest trackRequest = TrackRequest.builder()
                .trackTitle("test track")
                .albumImageUrl("url/track")
                .spotifyTrackId(spotifyTrackId)
                .spotifyTrackHref("href string")
                .trackArtistList(List.of("test artist"))
                .build();

        PlaylistTrackSaveRequestDto requestDto = PlaylistTrackSaveRequestDto.builder()
                .playlistIdList(List.of(1L,2L,3L))
                .playlistTitle("플리 타이틀")
                .alarmStartTime(LocalTime.now())
                .listSequence(1)
                .trackList(List.of(trackRequest))
                .build();
        Playlist playlist = requestDto.toPlaylistEntity();

        doReturn(userEmail)
                .when(jwtTokenProvider).getUserPk(accessToken);
        doReturn(Optional.of(user))
                .when(userRepository).findByEmail(userEmail);
        doReturn(Optional.of(playlist))
                .when(playlistRepository).findById(anyLong());

        //when
        final MessageResponse result = playlistTrackService.savePlaylistTrackList(requestDto, accessToken);

        //then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    @Test
    void 트랙_저장_성공_playlistIdList_비어있지않고_track_존재하면_플레이리스트_트랙_추가() throws AccountException {
        //given
        final String accessToken = "accessToken";
        final String userEmail = "test@email.com";
        final User user = User.builder().email(userEmail).build();

        TrackRequest trackRequest = TrackRequest.builder()
                .trackTitle("test track")
                .albumImageUrl("url/track")
                .spotifyTrackId(spotifyTrackId)
                .spotifyTrackHref("href string")
                .trackArtistList(List.of("test artist"))
                .build();
        Track track = trackRequest.toTrackEntity();

        PlaylistTrackSaveRequestDto requestDto = PlaylistTrackSaveRequestDto.builder()
                .playlistIdList(List.of(1L,2L,3L))
                .playlistTitle("플리 타이틀")
                .alarmStartTime(LocalTime.now())
                .listSequence(1)
                .trackList(List.of(trackRequest))
                .build();
        Playlist playlist = requestDto.toPlaylistEntity();

        doReturn(userEmail)
                .when(jwtTokenProvider).getUserPk(accessToken);
        doReturn(Optional.of(user))
                .when(userRepository).findByEmail(userEmail);
        doReturn(Optional.of(track))
                .when(trackRepository).findBySpotifyTrackId(trackRequest.getSpotifyTrackId());
        doReturn(Optional.of(playlist))
                .when(playlistRepository).findById(anyLong());

        //when
        final MessageResponse result = playlistTrackService.savePlaylistTrackList(requestDto, accessToken);

        //then
        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    @Test
    void 트랙_저장_실패_playlistIdList_존재하지않는_플레이리스트_포함() throws PlaylistException {
        final String accessToken = "accessToken";
        final String userEmail = "test@email.com";
        final User user = User.builder().email(userEmail).build();

        TrackRequest trackRequest = TrackRequest.builder()
                .trackTitle("test track")
                .albumImageUrl("url/track")
                .spotifyTrackId(spotifyTrackId)
                .spotifyTrackHref("href string")
                .trackArtistList(List.of("test artist"))
                .build();

        PlaylistTrackSaveRequestDto requestDto = PlaylistTrackSaveRequestDto.builder()
                .playlistIdList(List.of(1L,2L,3L))
                .playlistTitle("플리 타이틀")
                .alarmStartTime(LocalTime.now())
                .listSequence(1)
                .trackList(List.of(trackRequest))
                .build();

        doReturn(userEmail)
                .when(jwtTokenProvider).getUserPk(accessToken);
        doReturn(Optional.of(user))
                .when(userRepository).findByEmail(userEmail);

        // when
        final Throwable thrown = catchThrowable(() -> playlistTrackService.savePlaylistTrackList(requestDto, accessToken));

        // then
        assertThat(thrown.getMessage()).isEqualTo("플레이리스트를 찾을 수 없습니다.");
    }

}