package com.team.comma.spotify.track.repository;

import com.team.comma.spotify.track.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {

}
