package com.team.comma.repository;

import com.team.comma.domain.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findAllByUser_Email(String email);

    @Query("SELECT SUM(t.durationTimeMs) FROM Playlist p "
        + "JOIN p.playlistTrackList pt "
        + "JOIN pt.track t "
        + "WHERE p.id = :playlistId")
    Long getDurationSumByPlaylistId(@Param("playlistId") Long playlistId);
}
