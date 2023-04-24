package com.team.comma.dto;

import com.team.comma.domain.PlaylistTrack;
import com.team.comma.domain.Track;
import com.team.comma.domain.TrackArtist;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public final class PlaylistTrackResponse {
    private final Long trackId;
    private final String trackTitle;
    private final Integer durationTimeMs;
    private final String albumImageUrl;
    private final Boolean trackAlarmFlag;

    private final List<PlaylistTrackArtistResponse> trackArtistList;

    private PlaylistTrackResponse(Track track, Boolean trackAlarmFlag, List<PlaylistTrackArtistResponse> artists) {
        this.trackId = track.getId();
        this.trackTitle = track.getTrackTitle();
        this.durationTimeMs = track.getDurationTimeMs();
        this.albumImageUrl = track.getAlbumImageUrl();
        this.trackAlarmFlag = trackAlarmFlag;
        this.trackArtistList = new ArrayList<>(artists);
    }

    public static PlaylistTrackResponse of(Track track, Boolean trackAlarmFlag, List<PlaylistTrackArtistResponse> artists) {
        return new PlaylistTrackResponse(track, trackAlarmFlag, artists);
    }

    public List<PlaylistTrackArtistResponse> getTrackArtistList() {
        return Collections.unmodifiableList(trackArtistList);
    }

}
