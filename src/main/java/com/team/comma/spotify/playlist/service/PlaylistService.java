package com.team.comma.spotify.playlist.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.*;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.track.service.TrackService;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.AccountException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.team.comma.common.constant.ResponseCodeEnum.*;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final TrackService trackService;

    private final PlaylistRepository playlistRepository;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public List<PlaylistResponse> getPlaylists(final String accessToken) throws AccountException {
        String userName = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userName)
            .orElseThrow(() -> new AccountException("정보가 올바르지 않습니다."));

        List<Playlist> playlists = playlistRepository.findAllByUserAndDelFlag(user, false);
        return createPlaylistResponse(playlists);
    }

    public List<PlaylistResponse> createPlaylistResponse(List<Playlist> playlists) {
        List<PlaylistResponse> result = new ArrayList<>();
        for (Playlist playlist : playlists) {
            List<PlaylistTrackResponse> trackList = createTrackResponse(
                playlist.getPlaylistTrackList());
            result.add(PlaylistResponse.of(playlist, trackList));
        }
        return result;
    }

    public List<PlaylistTrackResponse> createTrackResponse(List<PlaylistTrack> playlistTrackList) {
        List<PlaylistTrackResponse> result = new ArrayList<>();
        for (PlaylistTrack playlistTrack : playlistTrackList) {
            List<PlaylistTrackArtistResponse> artistList = trackService.createArtistResponse(
                playlistTrack.getTrack().getTrackArtistList());
            result.add(PlaylistTrackResponse.of(playlistTrack.getTrack(),
                playlistTrack.getTrackAlarmFlag(), artistList));
        }
        return result;
    }

    @Transactional
    public MessageResponse updatePlaylist(PlaylistUpdateRequest playlistUpdateRequest) {

        Playlist playlist = playlistRepository.findById(playlistUpdateRequest.getId()).orElseThrow(
            () -> new EntityNotFoundException("해당 플레이리스트가 없습니다."));
        playlist.updatePlaylist(playlistUpdateRequest);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    @Transactional
    public MessageResponse updatePlaylistAlarmFlag(long playlistId, boolean alarmFlag) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistException("플레이리스트가 존재하지 않습니다."));

        playlistRepository.updateAlarmFlag(playlistId, alarmFlag);
        return MessageResponse.of(PLAYLIST_ALARM_UPDATED);
    }

    @Transactional
    public MessageResponse updatePlaylistsDelFlag(List<Long> playlistIdList) {
        for(Long playlistId : playlistIdList){
            Playlist playlist = playlistRepository.findById(playlistId)
                    .orElseThrow(() -> new PlaylistException("플레이리스트가 존재하지 않습니다."));
        }
        for(Long playlistId : playlistIdList){
            playlistRepository.deletePlaylist(playlistId);
        }
        return MessageResponse.of(PLAYLIST_DELETED);
    }

    public MessageResponse<Integer> getTotalDurationTimeMsByPlaylist(Long playlistId) {
        return MessageResponse.of(
                REQUEST_SUCCESS,
                playlistRepository.getTotalDurationTimeMsWithPlaylistId(playlistId)
        );
    }

}
