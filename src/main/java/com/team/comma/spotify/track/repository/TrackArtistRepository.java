package com.team.comma.spotify.track.repository;

import com.team.comma.spotify.track.domain.TrackArtist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackArtistRepository extends JpaRepository<TrackArtist, Long> {
}
