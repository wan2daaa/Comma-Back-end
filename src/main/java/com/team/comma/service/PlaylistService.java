package com.team.comma.service;

import com.team.comma.domain.Playlist;
import com.team.comma.domain.PlaylistTrack;
import com.team.comma.domain.TrackArtist;
import com.team.comma.dto.PlaylistResponse;
import com.team.comma.dto.PlaylistTrackArtistResponse;
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

    private final PlaylistTrackRepository playlistTrackRepository;
    private final PlaylistRepository playlistRepository;

    public List<Playlist> getPlaylist(final String email){
        return playlistRepository.findAllByUser_Email(email);
    }

    public List<PlaylistTrack> getPlaylistTrack(final Long id){
        return playlistTrackRepository.findAllByPlaylist_Id(id);
    }

    public List<PlaylistResponse> getPlaylistResponse(final String email) {
        List<Playlist> playlists = getPlaylist(email); // email로 playlist 조회
        return createPlaylist(playlists);
    }

    public List<PlaylistResponse> createPlaylist(List<Playlist> playlists){
        List<PlaylistResponse> result = new ArrayList<>();
        for(Playlist playlist : playlists){
            List<PlaylistTrackResponse> trackList = createTrackList(playlist.getPlaylistTrackList()); // playlist의 track list
            result.add(PlaylistResponse.of(playlist, trackList));
        }
        return result;
    }

    public List<PlaylistTrackResponse> createTrackList(List<PlaylistTrack> playlistTrackList){
        List<PlaylistTrackResponse> result = new ArrayList<>();
        for (PlaylistTrack playlistTrack : playlistTrackList) {
            List<PlaylistTrackArtistResponse> artistList = createArtistList(playlistTrack.getTrack().getTrackArtistList()); // track의 artist list
            result.add(PlaylistTrackResponse.of(playlistTrack.getTrack(), playlistTrack.getTrackAlarmFlag(), artistList));
        }
        return result;
    }

    public List<PlaylistTrackArtistResponse> createArtistList(List<TrackArtist> artistList){
        List<PlaylistTrackArtistResponse> result = new ArrayList<>();
        for (TrackArtist artist : artistList){
            result.add(PlaylistTrackArtistResponse.of(artist));
        }
        return result;
    }
}
