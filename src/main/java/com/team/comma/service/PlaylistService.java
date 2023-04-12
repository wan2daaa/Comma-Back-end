package com.team.comma.service;

import com.team.comma.domain.Playlist;
import com.team.comma.domain.PlaylistTrack;
import com.team.comma.domain.Track;
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

    public List<PlaylistResponse> getPlaylistResponse(String email) {
        List<PlaylistResponse> result = new ArrayList<>();
        List<PlaylistTrackResponse> tracks = new ArrayList<>();

        List<Playlist> userPlaylist = getPlaylist(email); // userEmail로 playlist 조회
        if(userPlaylist.size() != 0){
            for(Playlist playlist : userPlaylist){
                List<PlaylistTrack> playlistTracks = getPlaylistTrack(playlist.getId()); // playlistId로 track 조회
                if(playlistTracks.size() != 0) {
                    for (PlaylistTrack playlistTrack : playlistTracks) {
                        tracks.add(createTrack(playlistTrack.getTrack()));
                    }
                }
                result.add(createPlaylist(playlist,tracks));
            }
        }
        return result;
    }

    public PlaylistTrackResponse createTrack(final Track track){
        return PlaylistTrackResponse.builder()
                .id(track.getId())
                .trackTitle(track.getTrackTitle())
                .durationMs(track.getDurationMs())
                .artistName(track.getArtistName())
                .albumName(track.getAlbumName())
                .albumImageUrl(track.getAlbumImageUrl())
                .alarmFlag(track.getAlarmFlag())
                .build();
    }

    public PlaylistResponse createPlaylist(final Playlist playlist, final List<PlaylistTrackResponse> tracks){
        return PlaylistResponse.builder()
                .playlistId(playlist.getId())
                .playlistTitle(playlist.getPlaylistTitle())
                .alarmFlag(playlist.isAlarmFlag())
                .alarmDay(playlist.getAlarmDay())
                .alarmTime(playlist.getAlarmTime())
                .tracks(tracks)
                .build();
    }
}
