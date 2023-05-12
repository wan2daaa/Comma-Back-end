package com.team.comma.spotify.playlist.service;

import static com.team.comma.common.constant.ResponseCodeTest.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistTrackSaveRequestDto;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
import com.team.comma.spotify.track.domain.Track;
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
        doThrow(EntityNotFoundException.class)
            .when(playlistTrackRepository)
            .findByTrackIdAndPlaylistId(anyLong(), anyLong());
        //when //then
        assertThatThrownBy(
            () -> playlistTrackService.removePlaylistAndTrack(Set.of(1L, 2L, 3L), 1L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 플레이리스트와_트랙들을_PlaylistTrackSaveRequestDto로_저장한다() throws AccountException {
        //given
        final String accessToken = "accessToken";
        final String userEmail = "test@email.com";

        PlaylistTrackSaveRequestDto requestDto = PlaylistTrackSaveRequestDto.builder()
            .playlistTitle("플리 타이틀")
            .alarmStartTime(LocalTime.now())
            .listSequence(1)
            .trackIdList(List.of(1L, 2L, 3L))
            .build();

        Playlist playlist = requestDto.toPlaylistEntity();

        doReturn(userEmail)
            .when(jwtTokenProvider).getUserPk(accessToken);

        doReturn(User.builder().email(userEmail).build())
            .when(userRepository).findByEmail(userEmail);

        doReturn(Optional.of(1))
            .when(playlistTrackRepository).findMaxPlaySequenceByPlaylistId(playlist.getId());

        doReturn(playlist)
            .when(playlistRepository).save(any(Playlist.class));

        doReturn(Optional.of(Track.builder().build()))
            .when(trackRepository).findById(anyLong());

        doReturn(PlaylistTrack.builder().build())
            .when(playlistTrackRepository).save(any(PlaylistTrack.class));

        //when
        MessageResponse messageResponse = playlistTrackService.savePlaylistTrackList(requestDto,
            accessToken);

        //then
        assertThat(messageResponse.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(messageResponse.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    @Test
    void 트랙이_존재하지않으면_EntityNotFoundException() throws AccountException {
        //given
        final String accessToken = "accessToken";
        final String userEmail = "test@email.com";

        User user = User.builder().email(userEmail).build();
        userRepository.save(user);

        PlaylistTrackSaveRequestDto requestDto = PlaylistTrackSaveRequestDto.builder()
            .playlistTitle("플리 타이틀")
            .alarmStartTime(LocalTime.now())
            .listSequence(1)
            .trackIdList(List.of(1L, 2L, 3L))
            .build();

        Playlist playlist = requestDto.toPlaylistEntity();

        doReturn(userEmail)
            .when(jwtTokenProvider).getUserPk(accessToken);
        doReturn(user)
            .when(userRepository).findByEmail(userEmail);

        //when
        //then
        assertThatThrownBy(
            () -> playlistTrackService.savePlaylistTrackList(requestDto, accessToken))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 사용자가_존재하지않으면_AccountException() {
        //given
        final String accessToken = "accessToken";

        PlaylistTrackSaveRequestDto requestDto = PlaylistTrackSaveRequestDto.builder()
            .playlistTitle("플리 타이틀")
            .alarmStartTime(LocalTime.now())
            .listSequence(1)
            .trackIdList(List.of(1L, 2L, 3L))
            .build();
        ;

        //when
        //then
        assertThatThrownBy(
            () -> playlistTrackService.savePlaylistTrackList(requestDto, accessToken))
            .isInstanceOf(AccountException.class);
    }
}