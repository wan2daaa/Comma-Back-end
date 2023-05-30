package com.team.comma.spotify.playlist.repository;

public interface PlaylistRepositoryCustom {

    int getTotalDurationTimeMsWithPlaylistId(Long playlistId);

    int findMaxListSequence();

    long updateAlarmFlag(long id, boolean alarmFlag);

    long deletePlaylist(long id);
}
