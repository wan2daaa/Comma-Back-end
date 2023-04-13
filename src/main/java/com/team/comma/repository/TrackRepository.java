package com.team.comma.repository;

import com.team.comma.domain.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {

    List<Track> findAllById(Long id);
}
