package com.team.comma.spotify.playlist.service;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistTrackSaveRequestDto;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.dto.TrackRequest;
import com.team.comma.spotify.track.repository.TrackRepository;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;

import java.util.Set;
import javax.security.auth.login.AccountException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistTrackService {

    private final PlaylistTrackRepository playlistTrackRepository;
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public MessageResponse removePlaylistAndTrack(Set<Long> trackIdList, Long playlistId) {
        int deleteCount = 0;

        for (Long trackId : trackIdList) {
            playlistTrackRepository.findByTrackIdAndPlaylistId(
                trackId, playlistId);

            deleteCount += playlistTrackRepository.
                deletePlaylistTrackByTrackIdAndPlaylistId(trackId, playlistId);
        }
        return MessageResponse.of(
            REQUEST_SUCCESS.getCode(),
            REQUEST_SUCCESS.getMessage(),
            deleteCount
        );
    }

    public MessageResponse savePlaylistTrackList(PlaylistTrackSaveRequestDto dto, String accessToken)
        throws AccountException {

        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User findUser = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (findUser == null) {
            throw new AccountException("사용자를 찾을 수 없습니다.");
        }

        if (dto.getPlaylistIdList().isEmpty()){
            Playlist playlist = dto.toPlaylistEntity();
            playlistRepository.save(playlist);

            for (TrackRequest trackRequest : dto.getTrackList()) {
                addTrackToPlaylist(playlist,trackRequest);
            }
        } else {
            for (Long playlistId : dto.getPlaylistIdList()){
                Playlist playlist = playlistRepository.findById(playlistId)
                        .orElseThrow(() -> new PlaylistException("플레이리스트를 찾을 수 없습니다."));

                for (TrackRequest trackRequest : dto.getTrackList()) {
                    addTrackToPlaylist(playlist,trackRequest);
                }
            }
        }

        return MessageResponse.of(
            REQUEST_SUCCESS.getCode(),
            REQUEST_SUCCESS.getMessage()
        );
    }

    public void addTrackToPlaylist(Playlist playlist, TrackRequest trackRequest){
        Track track = trackRepository.findBySpotifyTrackId(trackRequest.getSpotifyTrackId())
                .orElse(trackRepository.save(trackRequest.toTrackEntity()));

        int maxPlaySequence = playlistTrackRepository.
                findMaxPlaySequenceByPlaylistId(playlist.getId())
                .orElse(0);

        PlaylistTrack eachPlaylistTrack = PlaylistTrack.builder()
                .playlist(playlist)
                .track(track)
                .playSequence(maxPlaySequence + 1)
                .build();
        playlistTrackRepository.save(eachPlaylistTrack);
    }

    public MessageResponse getPlaylistTracks(long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistException("플레이리스트를 찾을 수 없습니다."));

        return MessageResponse.of(REQUEST_SUCCESS,
                playlistTrackRepository.getPlaylistTracksByPlaylist(playlist));
    }

}
