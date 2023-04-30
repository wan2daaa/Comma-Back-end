package com.team.comma.spotify.playlist.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.team.comma.spotify.playlist.controller.PlaylistController;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackArtistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackResponse;
import com.team.comma.spotify.playlist.service.PlaylistService;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackArtist;
import java.util.Arrays;
import java.util.List;
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

@ExtendWith(MockitoExtension.class)
class PlaylistControllerTest {

    @InjectMocks
    PlaylistController playlistController;

    @Mock
    PlaylistService playlistService;

    MockMvc mockMvc;
    Gson gson;
    private String userEmail = "email@naver.com";

    @BeforeEach
    void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(playlistController)
            .build();
    }

    @Test
    void 사용자플레이리스트조회_이메일없음() throws Exception {
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
    void 사용자플레이리스트조회_성공() throws Exception {
        // given
        final String url = "/userPlaylist";
        List<PlaylistTrackArtistResponse> trackArtistList = Arrays.asList(
            PlaylistTrackArtistResponse.of(TrackArtist.builder().build()),
            PlaylistTrackArtistResponse.of(TrackArtist.builder().build()));

        List<PlaylistTrackResponse> trackList = Arrays.asList(
            PlaylistTrackResponse.of(Track.builder().build(), true, trackArtistList),
            PlaylistTrackResponse.of(Track.builder().build(), true, trackArtistList),
            PlaylistTrackResponse.of(Track.builder().build(), true, trackArtistList));

        doReturn(Arrays.asList(
            PlaylistResponse.of(Playlist.builder().build(), trackList),
            PlaylistResponse.of(Playlist.builder().build(), trackList),
            PlaylistResponse.of(Playlist.builder().build(), trackList)
        )).when(playlistService).getPlaylistResponse(userEmail);

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get(url).header("email", userEmail));

        // then
        resultActions.andExpect(status().isOk());

        final List<PlaylistResponse> result = playlistService.getPlaylistResponse(userEmail);
        assertThat(result).hasSize(3);
    }
}
