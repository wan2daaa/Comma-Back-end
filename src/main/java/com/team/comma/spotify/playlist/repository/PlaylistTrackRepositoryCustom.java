package com.team.comma.spotify.playlist.repository;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.dto.PlaylistTrackResponse;

import java.util.List;

public interface PlaylistTrackRepositoryCustom {

    List<PlaylistTrackResponse> getPlaylistTracksByPlaylist(Playlist playlist);
}
