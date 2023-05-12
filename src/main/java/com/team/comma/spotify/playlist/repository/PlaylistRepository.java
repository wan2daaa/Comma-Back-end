package com.team.comma.spotify.playlist.repository;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.user.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaylistRepository extends JpaRepository<Playlist, Long>,
    PlaylistRepositoryCustom {

    List<Playlist> findAllByUser(User user);

    @Modifying
    @Query("update Playlist p set p.alarmFlag = :alarmFlag where p.id = :id")
    int updateAlarmFlag(@Param("id") long id, @Param("alarmFlag") boolean alarmFlag);


}
