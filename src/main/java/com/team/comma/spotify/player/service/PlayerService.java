package com.team.comma.spotify.player.service;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.player.dto.PlayerResponseDto;
import com.team.comma.spotify.search.support.SpotifyAuthorization;
import com.team.comma.spotify.search.support.SpotifySearchCommand;
import com.team.comma.spotify.track.repository.TrackRepository;
import jakarta.persistence.EntityNotFoundException;
import javax.security.auth.login.AccountException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final SpotifyAuthorization spotifyAuthorization;

    private final TrackRepository trackRepository;

    private final SpotifySearchCommand spotifySearchCommand;

    public MessageResponse startAndResumePlayer(long trackId) throws AccountException {
        SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
        String spotifyTrackId = getSpotifyTrackIdWithTrackId(trackId);

        GetTrackRequest getTrackRequest = spotifyApi.getTrack(spotifyTrackId).build();
        Object executeResult = spotifySearchCommand.executeCommand(getTrackRequest);

        if (executeResult instanceof SpotifyApi) {
            return startAndResumePlayer(trackId);
        }
        String accessToken = spotifyApi.getAccessToken();

        Track findTrack = (Track) executeResult;
        String uri = findTrack.getUri();

        return MessageResponse.of(REQUEST_SUCCESS, PlayerResponseDto.of(accessToken, uri));
    }

    private String getSpotifyTrackIdWithTrackId(long trackId) {
        return trackRepository.findById(trackId)
            .orElseThrow(EntityNotFoundException::new)
            .getSpotifyTrackId();
    }
}

