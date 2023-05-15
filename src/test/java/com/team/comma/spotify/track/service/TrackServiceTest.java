package com.team.comma.spotify.track.service;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.repository.TrackRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

    @InjectMocks
    TrackService trackService;

    @Mock
    PlaylistTrackRepository playlistTrackRepository;

    @Mock
    TrackRepository trackRepository;

    @Test
    void 트랙의_알람설정을_바꾼다(){
        //given
        doReturn(Optional.of(Track.class))
            .when(trackRepository).findById(anyLong());
        doReturn(1L)
            .when(playlistTrackRepository).changeAlarmFlagWithTrackId(anyLong());

        //when
        MessageResponse messageResponse = trackService.updateAlarmFlag(anyLong());

        //then
        assertThat(messageResponse).isNotNull();
        assertThat(messageResponse.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(messageResponse.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    @Test
    void 트랙의_알림설정을_바꾼다_실패(){
        //given
        doThrow(EntityNotFoundException.class)
            .when(trackRepository).findById(anyLong());

        //when //then
        assertThatThrownBy(() -> trackService.updateAlarmFlag(anyLong()))
            .isInstanceOf(EntityNotFoundException.class);

    }
}