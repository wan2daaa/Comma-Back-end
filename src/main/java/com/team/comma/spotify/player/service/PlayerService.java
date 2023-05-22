package com.team.comma.spotify.player.service;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.player.dto.PlayerResponse;
import com.team.comma.spotify.search.support.SpotifyAuthorization;
import com.team.comma.spotify.track.repository.TrackRepository;
import jakarta.persistence.EntityNotFoundException;
import javax.security.auth.login.AccountException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final SpotifyAuthorization spotifyAuthorization;
    private final TrackRepository trackRepository;

    public MessageResponse startAndResumePlayer(long trackId) {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        String spotifyTrackId = getSpotifyTrackIdWithTrackId(trackId);
        String accessToken = spotifyApi.getAccessToken();


        return MessageResponse.of(REQUEST_SUCCESS, PlayerResponse.of(accessToken, spotifyTrackId));
    }

    private String getSpotifyTrackIdWithTrackId(long trackId) {
        return trackRepository.findById(trackId)
            .orElseThrow(EntityNotFoundException::new)
            .getSpotifyTrackId();
    }
}

