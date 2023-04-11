package com.team.comma.dto;

import com.team.comma.domain.Playlist;
import com.team.comma.domain.Track;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class PlaylistResponse {

    private final Long playlistId;
    private final String playlistTitle;
    private final Boolean alarmFlag;
    private final DayOfWeek alarmDay;
    private final LocalTime alarmTime;

    private final List<PlaylistTrackResponse> tracks;

}
