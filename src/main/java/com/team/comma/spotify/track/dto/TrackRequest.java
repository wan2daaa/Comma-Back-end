package com.team.comma.spotify.track.dto;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackArtist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TrackRequest {

    private String trackTitle;

    private String albumImageUrl;

    private String spotifyTrackId;

    private String spotifyTrackHref;

    private List<String> trackArtistList;

    public Track toTrackEntity() {
        return Track.builder()
                .trackTitle(trackTitle)
                .albumImageUrl(albumImageUrl)
                .spotifyTrackId(spotifyTrackId)
                .spotifyTrackHref(spotifyTrackHref)
                .trackArtistList(buildTrackArtistList(trackArtistList))
                .build();
    }
    public List<TrackArtist> buildTrackArtistList(List<String> artistList) {
        ArrayList<TrackArtist> trackArtistList = new ArrayList<>();
        for (String artist : artistList){
            trackArtistList.add(TrackArtist.builder().artistName(artist).build());
        }
        return trackArtistList;
    }
}
