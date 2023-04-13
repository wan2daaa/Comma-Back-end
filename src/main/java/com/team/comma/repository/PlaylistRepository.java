package com.team.comma.repository;

import com.team.comma.domain.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findAllByUser_Email(String email);

}
