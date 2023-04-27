package com.team.comma.spotify.playlist.dto;

import com.team.comma.spotify.track.domain.TrackArtist;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class PlaylistTrackArtistResponse {

    private final Long artistId;
    private final String artistName;

    private PlaylistTrackArtistResponse(TrackArtist trackArtist) {
        this.artistId = trackArtist.getId();
        this.artistName = trackArtist.getArtistName();
    }

    public static PlaylistTrackArtistResponse of(TrackArtist trackArtist) {
        return new PlaylistTrackArtistResponse(trackArtist);
    }

}
