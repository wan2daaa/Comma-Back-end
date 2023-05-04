package com.team.comma.spotify.playlist.repository;

import com.team.comma.spotify.playlist.domain.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findAllByUser_Email(String email);

    @Query("SELECT COALESCE(SUM(t.durationTimeMs),0) FROM Playlist p "
        + "JOIN p.playlistTrackList pt "
        + "JOIN pt.track t "
        + "WHERE p.id = :playlistId")
    int getTotalDurationTimeMsWithPlaylistId(@Param("playlistId") Long playlistId);

    //listSequence중 가장 큰 값 리턴
    @Query("SELECT COALESCE(MAX(p.listSequence),0) FROM Playlist p")
    int findMaxListSequence();
}
