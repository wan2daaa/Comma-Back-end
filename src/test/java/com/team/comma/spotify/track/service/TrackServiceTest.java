package com.team.comma.spotify.track.service;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static com.team.comma.common.constant.ResponseCodeEnum.SIMPLE_REQUEST_FAILURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
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


    @Test
    void 트랙의_알람설정을_바꾼다(){
        //given
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
        doReturn(0L)
            .when(playlistTrackRepository).changeAlarmFlagWithTrackId(anyLong());

        //when
        MessageResponse messageResponse = trackService.updateAlarmFlag(anyLong());

        //then
        assertThat(messageResponse).isNotNull();
        assertThat(messageResponse.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(messageResponse.getMessage()).isEqualTo(SIMPLE_REQUEST_FAILURE.getMessage());

    }
}