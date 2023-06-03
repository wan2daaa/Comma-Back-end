package com.team.comma.spotify.track.repository;

import com.team.comma.spotify.track.domain.Track;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Long> {

    List<Track> findAllById(Long id);

    Optional<Track> findBySpotifyTrackId(String spotifyTrackId);
}
