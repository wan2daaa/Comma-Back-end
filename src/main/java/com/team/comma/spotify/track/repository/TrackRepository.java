package com.team.comma.spotify.track.repository;

import com.team.comma.spotify.track.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrackRepository extends JpaRepository<Track, Long> {

    List<Track> findAllById(Long id);

}
