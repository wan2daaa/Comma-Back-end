package com.team.comma.spotify.playlist.service;

import static com.team.comma.common.constant.ResponseCode.PLAYLIST_ALARM_UPDATED;
import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackArtistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackResponse;
import com.team.comma.spotify.playlist.dto.PlaylistUpdateRequest;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.track.domain.TrackArtist;
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

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public List<PlaylistResponse> getPlaylists(final String accessToken) throws AccountException {
        String userName = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userName)
            .orElseThrow(() -> new AccountException("정보가 올바르지 않습니다."));

        List<Playlist> playlists = playlistRepository.findAllByUser(user); // email로 playlist 조회
        return createPlaylistResponse(playlists);
    }

    public List<PlaylistResponse> createPlaylistResponse(List<Playlist> playlists) {
        List<PlaylistResponse> result = new ArrayList<>();
        for (Playlist playlist : playlists) {
            List<PlaylistTrackResponse> trackList = createTrackResponse(
                playlist.getPlaylistTrackList()); // playlist의 track list
            result.add(PlaylistResponse.of(playlist, trackList));
        }
        return result;
    }

    public List<PlaylistTrackResponse> createTrackResponse(List<PlaylistTrack> playlistTrackList) {
        List<PlaylistTrackResponse> result = new ArrayList<>();
        for (PlaylistTrack playlistTrack : playlistTrackList) {
            List<PlaylistTrackArtistResponse> artistList = createArtistResponse(
                playlistTrack.getTrack().getTrackArtistList());
            result.add(PlaylistTrackResponse.of(playlistTrack.getTrack(),
                playlistTrack.getTrackAlarmFlag(), artistList));
        }
        return result;
    }

    public List<PlaylistTrackArtistResponse> createArtistResponse(List<TrackArtist> artistList) {
        List<PlaylistTrackArtistResponse> result = new ArrayList<>();
        for (TrackArtist artist : artistList) {
            result.add(PlaylistTrackArtistResponse.of(artist));
        }
        return result;
    }

    @Transactional
    public MessageResponse updateAlarmFlag(long playlistId, boolean alarmFlag)
        throws PlaylistException {

        playlistRepository.updateAlarmFlag(playlistId, alarmFlag);
        return MessageResponse.of(PLAYLIST_ALARM_UPDATED, "알람 설정이 변경되었습니다.");
    }


    public MessageResponse<Integer> getTotalDurationTimeMsByPlaylist(Long playlistId) {
        return MessageResponse.of(
            REQUEST_SUCCESS,
            playlistRepository.getTotalDurationTimeMsWithPlaylistId(playlistId)
        );
    }

    @Transactional
    public MessageResponse updatePlaylist(PlaylistUpdateRequest playlistUpdateRequest) {

        Playlist playlist = playlistRepository.findById(playlistUpdateRequest.getId()).orElseThrow(
            () -> new EntityNotFoundException("해당 플레이리스트가 없습니다."));
        playlist.updatePlaylist(playlistUpdateRequest);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

}
