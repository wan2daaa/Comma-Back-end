package com.team.comma.spotify.search.support;

import com.team.comma.spotify.search.exception.SpotifyException;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.exceptions.detailed.UnauthorizedException;
import se.michaelthelin.spotify.requests.data.AbstractDataRequest;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SpotifySearchCommand {
    private final SpotifyAuthorization spotifyAuthorization;

    public Object executeCommand(AbstractDataRequest searchRequest) {
        try {
            return searchRequest.execute();
        } catch (UnauthorizedException e) {
            return spotifyAuthorization.refreshSpotifyToken();
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new SpotifyException(String.format("Spotify에 알 수 없는 오류가 발생했습니다 : %s" , e.getMessage()));
        }
    }
}
