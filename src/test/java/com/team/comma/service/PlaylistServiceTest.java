package com.team.comma.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.Playlist;
import com.team.comma.domain.PlaylistTrack;
import com.team.comma.domain.Track;
import com.team.comma.domain.User;
import com.team.comma.dto.ArtistResponse;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.OAuthRequest;
import com.team.comma.dto.PlaylistResponse;
import com.team.comma.repository.PlaylistRepository;
import com.team.comma.repository.PlaylistTrackRepository;
import com.team.comma.repository.TrackRepository;
import com.team.comma.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {
    @InjectMocks
    private PlaylistService playlistService;
    @Mock
    private PlaylistTrackRepository playlistTrackRepository;
    @Mock
    private PlaylistRepository playlistRepository;
    @Mock
    private TrackRepository trackRepository;
    @Mock
    private UserRepository userRepository;

    private String userEmail = "email@naver.com";

    @Test
    public void 플레이리스트_조회() {
        // given
        doReturn(Arrays.asList(
                Playlist.builder().build(),
                Playlist.builder().build(),
                Playlist.builder().build()
        )).when(playlistRepository).findAllByUser_Email(userEmail);

        // when
        List<Playlist> result = playlistService.getPlaylist(userEmail);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 플레이리스트_곡조회() {
        // given
        doReturn(Arrays.asList(
                PlaylistTrack.builder().build(),
                PlaylistTrack.builder().build(),
                PlaylistTrack.builder().build()
        )).when(playlistTrackRepository).findAllByPlaylist_Id(123L);

        // when
        List<PlaylistTrack> result = playlistService.getPlaylistTrack(123L);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 사용자플레이리스트_조회() {
        // given
        doReturn(Arrays.asList(
                PlaylistResponse.builder().build(),
                PlaylistResponse.builder().build(),
                PlaylistResponse.builder().build()
        )).when(playlistTrackRepository).findAllByPlaylist_Id(123L);

        // when
        List<PlaylistTrack> result = playlistService.getPlaylistTrack(123L);

        // then
        assertThat(result.size()).isEqualTo(3);
    }
}
