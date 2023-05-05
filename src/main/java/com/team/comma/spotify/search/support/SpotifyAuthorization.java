package com.team.comma.spotify.search.support;

import com.team.comma.util.jwt.support.CreationAccessToken;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;

@Component
public class SpotifyAuthorization {

    SpotifyApi spotifyApi = new SpotifyApi.Builder().build();
    public SpotifyApi refreshSpotifyToken() {
        CreationAccessToken creationAccessToken = new CreationAccessToken();
        spotifyApi = new SpotifyApi.Builder().setAccessToken(creationAccessToken.accessToken()).build();

        return spotifyApi;
    }

    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

}
