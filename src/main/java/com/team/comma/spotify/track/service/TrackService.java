package com.team.comma.spotify.track.service;

import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistTrackArtistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackResponse;
import com.team.comma.spotify.track.domain.TrackArtist;
import com.team.comma.spotify.track.repository.TrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final TrackRepository trackRepository;

    public List<PlaylistTrackArtistResponse> getTrackArtistResponseList(final List<TrackArtist> artists){
        List<PlaylistTrackArtistResponse> result = new ArrayList<>();
        for (TrackArtist artist : artists){
            result.add(PlaylistTrackArtistResponse.of(artist));
        }
        return result;
    }

}
