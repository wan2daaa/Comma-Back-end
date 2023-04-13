package com.team.comma.dto;

import com.team.comma.domain.Playlist;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public final class PlaylistResponse {

    private final Long playlistId;
    private final String playlistTitle;
    private final Boolean alarmFlag;
    private final DayOfWeek alarmDay;
    private final LocalTime alarmTime;

    private final List<PlaylistTrackResponse> tracks;

    private PlaylistResponse(Playlist playlist, List<PlaylistTrackResponse> tracks) {
        this.playlistId = playlist.getId();
        this.playlistTitle = playlist.getPlaylistTitle();
        this.alarmFlag = playlist.isAlarmFlag();
        this.alarmDay = playlist.getAlarmDay();
        this.alarmTime = playlist.getAlarmTime();
        this.tracks = tracks;
    }

    public static PlaylistResponse of(Playlist playlist, List<PlaylistTrackResponse> tracks) {
        return new PlaylistResponse(playlist, tracks);
    }

}
