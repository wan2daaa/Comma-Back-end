package com.team.comma.spotify.track.repository;

import com.team.comma.spotify.track.domain.Track;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Long> {

    Optional<Track> findBySpotifyTrackId(String spotifyTrackId);

}
