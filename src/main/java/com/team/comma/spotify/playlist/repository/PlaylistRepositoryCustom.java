package com.team.comma.spotify.playlist.repository;

import org.springframework.data.repository.query.Param;

public interface PlaylistRepositoryCustom {

    int getTotalDurationTimeMsWithPlaylistId(@Param("playlistId") Long playlistId);

    int findMaxListSequence();

}
