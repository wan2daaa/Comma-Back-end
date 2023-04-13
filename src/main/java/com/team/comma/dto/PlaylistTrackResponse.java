package com.team.comma.dto;

import com.team.comma.domain.Track;
import lombok.*;

@Getter
@RequiredArgsConstructor
public final class PlaylistTrackResponse {
    private final Long id;
    private final String trackTitle;
    private final Integer durationMs;
    private final String artistName;
    private final String albumName;
    private final String albumImageUrl;
    private final Boolean alarmFlag;

    private PlaylistTrackResponse(Track track) {
        this.id = track.getId();
        this.trackTitle = track.getTrackTitle();
        this.durationMs = track.getDurationMs();
        this.artistName = track.getArtistName();
        this.albumName = track.getAlbumName();
        this.albumImageUrl = track.getAlbumImageUrl();
        this.alarmFlag = track.getAlarmFlag();
    }

    public static PlaylistTrackResponse of(Track track) {
        return new PlaylistTrackResponse(track);
    }
}
