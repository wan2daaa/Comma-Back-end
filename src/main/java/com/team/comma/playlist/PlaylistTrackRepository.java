package com.team.comma.playlist;

import com.team.comma.playlist.domain.PlaylistTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long> {

    List<PlaylistTrack> findAllByPlaylist_Id(Long playlistId);

}
