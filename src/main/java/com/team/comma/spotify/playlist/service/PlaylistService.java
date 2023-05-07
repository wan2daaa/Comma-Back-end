package com.team.comma.spotify.playlist.service;

import static com.team.comma.common.constant.ResponseCodeTest.REQUEST_SUCCESS;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.domain.PlaylistTrack;
import com.team.comma.spotify.playlist.dto.PlaylistRequest;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackArtistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackResponse;
import com.team.comma.spotify.playlist.repository.PlaylistRepository;
import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
import com.team.comma.spotify.track.domain.TrackArtist;
import com.team.comma.user.domain.User;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import javax.security.auth.login.AccountException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistTrackRepository playlistTrackRepository;
    private final PlaylistRepository playlistRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    public List<Playlist> getPlaylist(final String email) {
        return playlistRepository.findAllByUser_Email(email);
    }

    public List<PlaylistTrack> getPlaylistTrack(final Long id) {
        return playlistTrackRepository.findAllByPlaylist_Id(id);
    }

    public List<PlaylistResponse> getPlaylistResponse(final String email) {
        List<Playlist> playlists = getPlaylist(email); // email로 playlist 조회
        return createPlaylistList(playlists);
    }

    public List<PlaylistResponse> createPlaylistList(List<Playlist> playlists) {
        List<PlaylistResponse> result = new ArrayList<>();
        for (Playlist playlist : playlists) {
            List<PlaylistTrackResponse> trackList = createTrackList(
                playlist.getPlaylistTrackList()); // playlist의 track list
            result.add(PlaylistResponse.of(playlist, trackList));
        }
        return result;
    }

    public List<PlaylistTrackResponse> createTrackList(List<PlaylistTrack> playlistTrackList) {
        List<PlaylistTrackResponse> result = new ArrayList<>();
        for (PlaylistTrack playlistTrack : playlistTrackList) {
            List<PlaylistTrackArtistResponse> artistList = createArtistList(
                playlistTrack.getTrack().getTrackArtistList()); // track의 artist list
            result.add(PlaylistTrackResponse.of(playlistTrack.getTrack(),
                playlistTrack.getTrackAlarmFlag(), artistList));
        }
        return result;
    }

    public List<PlaylistTrackArtistResponse> createArtistList(List<TrackArtist> artistList) {
        List<PlaylistTrackArtistResponse> result = new ArrayList<>();
        for (TrackArtist artist : artistList) {
            result.add(PlaylistTrackArtistResponse.of(artist));
        }
        return result;
    }

    public MessageResponse<Integer> getTotalDurationTimeMsByPlaylist(Long playlistId) {
        return MessageResponse.of(
            REQUEST_SUCCESS.getCode(),
            REQUEST_SUCCESS.getMessage(),
            playlistRepository.getTotalDurationTimeMsWithPlaylistId(playlistId)
        );
    }

    public MessageResponse createPlaylist
        (
            PlaylistRequest playlistRequest,
            String accessToken
        ) throws AccountException {
        String userEmail = jwtTokenProvider.getUserPk(accessToken);
        User findUser = userRepository.findByEmail(userEmail);

        if (findUser == null) {
            throw new AccountException("사용자를 찾을 수 없습니다.");
        }

        playlistRequest.setUser(findUser);
        playlistRequest.setListSequence(playlistRepository.findMaxListSequence() + 1);

        Playlist playlist = playlistRequest.toEntity();

        Playlist save = playlistRepository.save(playlist);
        return MessageResponse.of(
            REQUEST_SUCCESS.getCode(),
            REQUEST_SUCCESS.getMessage()
        );
    }

    @Transactional
    public MessageResponse updatePlaylist(PlaylistRequest playlistRequest) {

        Playlist playlist = playlistRepository.findById(playlistRequest.getId()).orElseThrow(
            () -> new EntityNotFoundException("해당 플레이리스트가 없습니다."));
        playlist.updatePlaylist(playlistRequest);

        return MessageResponse.of(
            REQUEST_SUCCESS.getCode(),
            REQUEST_SUCCESS.getMessage()
        );
    }
}
