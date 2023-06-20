package com.team.comma.spotify.playlist.repository;

import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.user.domain.User;

import java.util.List;

public interface PlaylistRepositoryCustom {

    int getTotalDurationTimeMsWithPlaylistId(Long playlistId);

    int findMaxListSequence();

    long updateAlarmFlag(long id, boolean alarmFlag);

    long deletePlaylist(long id);

    List<PlaylistResponse> getPlaylistsByUser(User user);
}
