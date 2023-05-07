package com.team.comma.spotify.playlist.dto;

import com.team.comma.spotify.track.domain.Track;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public final class PlaylistTrackResponse {

    private final long trackId;
    private final String trackTitle;
    private final Integer durationTimeMs;
    private final String albumImageUrl;
    private final boolean trackAlarmFlag;

    private final List<PlaylistTrackArtistResponse> trackArtistList;

    private PlaylistTrackResponse(Track track, boolean trackAlarmFlag, List<PlaylistTrackArtistResponse> trackArtistList) {
        this.trackId = track.getId();
        this.trackTitle = track.getTrackTitle();
        this.durationTimeMs = track.getDurationTimeMs();
        this.albumImageUrl = track.getAlbumImageUrl();
        this.trackAlarmFlag = trackAlarmFlag;
        this.trackArtistList = new ArrayList<>(trackArtistList);
    }

    public static PlaylistTrackResponse of(Track track, boolean trackAlarmFlag, List<PlaylistTrackArtistResponse> trackArtistList) {
        return new PlaylistTrackResponse(track, trackAlarmFlag, trackArtistList);
    }

    public List<PlaylistTrackArtistResponse> getTrackArtistList() {
        return Collections.unmodifiableList(trackArtistList);
    }

}
