package com.team.comma.spotify.player.service;

import static com.google.gson.JsonParser.parseString;
import static com.team.comma.common.constant.ResponseCodeEnum.*;

import com.team.comma.common.constant.ResponseCodeEnum;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.search.exception.SpotifyException;
import com.team.comma.spotify.search.support.SpotifyAuthorization;
import com.team.comma.spotify.track.repository.TrackRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.Device;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final SpotifyAuthorization spotifyAuthorization;

    private final TrackRepository trackRepository;

    public MessageResponse startAndResumePlayer(long trackId) {
        try {
            SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();

            String deviceId = getDeviceId(spotifyApi);
            String spotifyTrackId = getSpotifyTrackIdWithTrackId(trackId);
            String spotifyTrackUri = getSpotifyTrackUri(spotifyTrackId, spotifyApi);

            makeStartAndResumePlayer(spotifyApi, deviceId, spotifyTrackUri);

        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new SpotifyException(SPOTIFY_FAILURE.getMessage());
        }

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public MessageResponse pausePlayer() {
        try {
            SpotifyApi spotifyApi = spotifyAuthorization.getSpotifyApi();
            String deviceId = getDeviceId(spotifyApi);

            makePausePlayer(spotifyApi, deviceId);

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new SpotifyException(SPOTIFY_FAILURE.getMessage());
        }
        return MessageResponse.of(REQUEST_SUCCESS);
    }

    private static void makePausePlayer(SpotifyApi spotifyApi, String deviceId)
        throws IOException, SpotifyWebApiException, ParseException {
        spotifyApi.pauseUsersPlayback()
            .device_id(deviceId)
            .build()
            .execute();
    }

    private static void makeStartAndResumePlayer(SpotifyApi spotifyApi, String deviceId, String spotifyTrackUri)
        throws IOException, SpotifyWebApiException, ParseException {
        spotifyApi.startResumeUsersPlayback()
            .device_id(deviceId)
            .uris(parseString(spotifyTrackUri).getAsJsonArray())
            .build()
            .execute();
    }


    private static String getSpotifyTrackUri(String spotifyTrackId, SpotifyApi spotifyApi)
        throws IOException, SpotifyWebApiException, ParseException {
        return convertToTrackUriString(
            spotifyApi.getTrack(spotifyTrackId).build().execute()
                .getUri()
        );
    }

    private String getSpotifyTrackIdWithTrackId(long trackId) {
        return trackRepository.findById(trackId)
            .orElseThrow(EntityNotFoundException::new)
            .getSpotifyTrackId();
    }

    public static String convertToTrackUriString(String trackUri) {
        String escapedUri = trackUri.replace("\"", "\\\""); // 큰따옴표 이스케이프 처리
        return "[\"" + escapedUri + "\"]";
    }

    private static String getDeviceId(SpotifyApi spotifyApi)
        throws IOException, SpotifyWebApiException, ParseException {
        if (getDevices(spotifyApi).length == 0) {
            throw new SpotifyException("사용가능한 디바이스가 없습니다.");
        }
        return getDevices(spotifyApi)[0].getId();
    }

    private static Device[] getDevices(SpotifyApi spotifyApi)
        throws IOException, SpotifyWebApiException, ParseException {
        return spotifyApi.getUsersAvailableDevices().build().execute();
    }



}

