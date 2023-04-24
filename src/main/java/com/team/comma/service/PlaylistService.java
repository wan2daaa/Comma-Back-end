package com.team.comma.service;

import com.team.comma.domain.Playlist;
import com.team.comma.domain.PlaylistTrack;
import com.team.comma.dto.PlaylistResponse;
import com.team.comma.dto.PlaylistTrackResponse;
import com.team.comma.repository.PlaylistRepository;
import com.team.comma.repository.PlaylistTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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
        List<Playlist> userPlaylist = getPlaylist(email); // userEmail로 playlist 조회
        List<PlaylistResponse> result = createPlaylist(userPlaylist);
        return result;
    }

    public List<PlaylistResponse> createPlaylist(List<Playlist> userPlaylist){
        List<PlaylistResponse> result = new ArrayList<>();
        for(Playlist playlist : userPlaylist){
            List<PlaylistTrack> playlistTracks = getPlaylistTrack(playlist.getId()); // playlistId로 track 조회
            List<PlaylistTrackResponse> tracks = createPlaylistTracks(playlistTracks);
            result.add(PlaylistResponse.of(playlist,tracks));
        }
        return result;
    }

    public List<PlaylistTrackResponse> createPlaylistTracks(List<PlaylistTrack> playlistTracks){
        List<PlaylistTrackResponse> result = new ArrayList<>();
        for (PlaylistTrack playlistTrack : playlistTracks) {
            result.add(PlaylistTrackResponse.of(playlistTrack, playlistTrack.getTrack()));
        }
        return result;
    }
}
