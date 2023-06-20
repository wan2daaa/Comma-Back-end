package com.team.comma.spotify.playlist.dto;

import com.team.comma.spotify.playlist.domain.Playlist;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public final class PlaylistResponse {

    private final long playlistId;
    private final String playlistTitle;
    private final boolean alarmFlag;
    private final LocalTime alarmStartTime;
    private final String repAlbumImageUrl;
    private final long trackCount;

    private PlaylistResponse(Playlist playlist, long trackCount, String repAlbumImageUrl) {
        this.playlistId = playlist.getId();
        this.playlistTitle = playlist.getPlaylistTitle();
        this.alarmFlag = playlist.getAlarmFlag();
        this.alarmStartTime = playlist.getAlarmStartTime();
        this.repAlbumImageUrl = repAlbumImageUrl;
        this.trackCount = trackCount;
    }

    public static PlaylistResponse of(Playlist playlist, long trackCount, String repAlbumImageUrl) {
        return new PlaylistResponse(playlist, trackCount, repAlbumImageUrl);
    }

}
