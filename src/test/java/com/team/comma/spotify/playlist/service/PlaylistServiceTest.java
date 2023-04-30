package com.team.comma.spotify.playlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
import com.team.comma.spotify.playlist.service.PlaylistService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlaylistServiceTest {

    @InjectMocks
    private PlaylistService playlistService;
    @Mock
    private PlaylistTrackRepository playlistTrackRepository;
    @Mock
    private PlaylistRepository playlistRepository;

    private String userEmail = "email@naver.com";

    @Test
    public void 플레이리스트_조회_성공() {
        // given
        doReturn(Arrays.asList(
            Playlist.builder().build(),
            Playlist.builder().build(),
            Playlist.builder().build()
        )).when(playlistRepository).findAllByUser_Email(userEmail);

        // when
        final List<Playlist> result = playlistService.getPlaylist(userEmail);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 플레이리스트_곡조회_성공() {
        // given
        doReturn(Arrays.asList(
            PlaylistTrack.builder().build(),
            PlaylistTrack.builder().build(),
            PlaylistTrack.builder().build()
        )).when(playlistTrackRepository).findAllByPlaylist_Id(123L);

        // when
        final List<PlaylistTrack> result = playlistService.getPlaylistTrack(123L);

        // then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 사용자플레이리스트_조회_성공() {
        // given
        doReturn(Arrays.asList(
            Playlist.builder().build(),
            Playlist.builder().build(),
            Playlist.builder().build()
        )).when(playlistTrackRepository).findAllByPlaylist_Id(123L);

        // when
        final List<PlaylistTrack> result = playlistService.getPlaylistTrack(123L);

        // then
        assertThat(result.size()).isEqualTo(3);
    }
}
