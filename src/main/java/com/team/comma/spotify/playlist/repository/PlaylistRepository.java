package com.team.comma.spotify.playlist.repository;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findAllByUser(User user);

    @Modifying
    @Query("update Playlist p set p.alarmFlag = :alarmFlag where p.id = :id")
    int updateAlarmFlag(@Param("id") long id, @Param("alarmFlag") boolean alarmFlag);

    @Query("SELECT COALESCE(SUM(t.durationTimeMs),0) FROM Playlist p "
        + "JOIN p.playlistTrackList pt "
        + "JOIN pt.track t "
        + "WHERE p.id = :playlistId")
    int getTotalDurationTimeMsWithPlaylistId(@Param("playlistId") Long playlistId);

    @Query("SELECT COALESCE(MAX(p.listSequence),0) FROM Playlist p")
    int findMaxListSequence();

}
