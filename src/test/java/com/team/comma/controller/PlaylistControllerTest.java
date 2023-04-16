package com.team.comma.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.comma.domain.Playlist;
import com.team.comma.domain.Track;
import com.team.comma.dto.ArtistResponse;
import com.team.comma.dto.PlaylistResponse;
import com.team.comma.dto.PlaylistTrackResponse;
import com.team.comma.service.PlaylistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PlaylistControllerTest {
    @InjectMocks
    PlaylistController playlistController;

    @Mock
    PlaylistService playlistService;

    MockMvc mockMvc;
    Gson gson;
    private String userEmail = "email@naver.com";
    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(playlistController)
                .build();
    }

    @Test
    public void 사용자플레이리스트조회_이메일없음() throws Exception {
        // given
        final String url = "/userPlaylist";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 사용자플레이리스트조회_성공() throws Exception {
        // given
        final String url = "/userPlaylist";
        List<PlaylistTrackResponse> tracks = Arrays.asList(
                PlaylistTrackResponse.of(Track.builder().build()),
                PlaylistTrackResponse.of(Track.builder().build()),
                PlaylistTrackResponse.of(Track.builder().build()));

        doReturn(Arrays.asList(
                PlaylistResponse.of(Playlist.builder().build(), tracks),
                PlaylistResponse.of(Playlist.builder().build(), tracks),
                PlaylistResponse.of(Playlist.builder().build(), tracks)
        )).when(playlistService).getPlaylistResponse(userEmail);

        // when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(url).header("email",userEmail));

        // then
        resultActions.andExpect(status().isOk());

        final List<PlaylistResponse> result = playlistService.getPlaylistResponse(userEmail);
        assertThat(result.size()).isEqualTo(3);
    }
}
