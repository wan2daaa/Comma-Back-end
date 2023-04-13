package com.team.comma.service;

import com.team.comma.domain.Playlist;
import com.team.comma.domain.PlaylistTrack;
import com.team.comma.dto.PlaylistResponse;
import com.team.comma.dto.PlaylistTrackResponse;
import com.team.comma.repository.PlaylistRepository;
import com.team.comma.repository.PlaylistTrackRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaylistService {

    final private PlaylistTrackRepository playlistTrackRepository;
    final private PlaylistRepository playlistRepository;

    public List<Playlist> getPlaylist(final String email){
        return playlistRepository.findAllByUser_Email(email);
    }

    public List<PlaylistTrack> getPlaylistTrack(final Long id){
        return playlistTrackRepository.findAllByPlaylist_Id(id);
    }

    public List<PlaylistResponse> getPlaylistResponse(final String email) {
        List<PlaylistResponse> result = new ArrayList<>();
        List<PlaylistTrackResponse> tracks = new ArrayList<>();

        List<Playlist> userPlaylist = getPlaylist(email); // userEmail로 playlist 조회
        if(userPlaylist.size() != 0){
            for(Playlist playlist : userPlaylist){
                List<PlaylistTrack> playlistTracks = getPlaylistTrack(playlist.getId()); // playlistId로 track 조회
                if(playlistTracks.size() != 0) {
                    for (PlaylistTrack playlistTrack : playlistTracks) {
                        tracks.add(PlaylistTrackResponse.of(playlistTrack.getTrack()));
                    }
                }
                result.add(PlaylistResponse.of(playlist,tracks));
            }
        }
        return result;
    }

}
