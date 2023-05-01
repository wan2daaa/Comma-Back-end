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

    private final Long playlistId;
    private final String playlistTitle;
    private final Boolean alarmFlag;
    private final LocalTime alarmStartTime;

    private final List<PlaylistTrackResponse> trackList;

    private PlaylistResponse(Playlist playlist, List<PlaylistTrackResponse> trackList) {
        this.playlistId = playlist.getId();
        this.playlistTitle = playlist.getPlaylistTitle();
        this.alarmFlag = playlist.getAlarmFlag();
        this.alarmStartTime = playlist.getAlarmStartTime();
        this.trackList = new ArrayList<>(trackList);
    }

    public static PlaylistResponse of(Playlist playlist, List<PlaylistTrackResponse> trackList) {
        return new PlaylistResponse(playlist, trackList);
    }

    public List<PlaylistTrackResponse> getTrackList() {
        return Collections.unmodifiableList(trackList);
    }

}
