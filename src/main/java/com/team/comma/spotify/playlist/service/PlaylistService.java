package com.team.comma.spotify.playlist.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.Exception.PlaylistException;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackArtistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackResponse;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.track.service.TrackService;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.team.comma.common.constant.ResponseCode.PLAYLIST_ALARM_UPDATED;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final TrackService trackService;

    private final PlaylistRepository playlistRepository;

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public List<PlaylistResponse> getPlaylists(final String accessToken) {
        String userName = jwtTokenProvider.getUserPk(accessToken);
        User user = userRepository.findByEmail(userName);
        List<Playlist> playlists = playlistRepository.findAllByUser(user);
        return getPlaylistResponse(playlists);
    }

    public List<PlaylistResponse> getPlaylistResponse(final List<Playlist> playlists){
        List<PlaylistResponse> result = new ArrayList<>();
        for(Playlist playlist : playlists){
            List<PlaylistTrackResponse> tracks = getTrackReponseList(playlist.getPlaylistTrackList());
            result.add(PlaylistResponse.of(playlist, tracks));
        }
        return result;
    }

    public List<PlaylistTrackResponse> getTrackReponseList(final List<PlaylistTrack> playlistTracks){
        List<PlaylistTrackResponse> result = new ArrayList<>();
        for (PlaylistTrack playlistTrack : playlistTracks) {
            List<PlaylistTrackArtistResponse> trackArtists = trackService.getTrackArtistResponseList(playlistTrack.getTrack().getTrackArtistList());
            result.add(PlaylistTrackResponse.of(playlistTrack.getTrack(), playlistTrack.getTrackAlarmFlag(), trackArtists));
        }
        return result;
    }

    @Transactional
    public MessageResponse updateAlarmFlag(final long playlistId, final boolean alarmFlag) throws PlaylistException{
        Optional<Playlist> optionalPlaylist = playlistRepository.findById(playlistId);
        Playlist playlist = optionalPlaylist.orElseThrow(() -> new PlaylistException("알람 설정 변경에 실패했습니다. 플레이리스트를 찾을 수 없습니다."));

        playlistRepository.updateAlarmFlag(playlistId, alarmFlag);
        return MessageResponse.of(PLAYLIST_ALARM_UPDATED, "알람 설정이 변경되었습니다.");
    }

}
