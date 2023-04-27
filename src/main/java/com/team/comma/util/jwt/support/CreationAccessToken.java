package com.team.comma.util.jwt.support;

import com.team.comma.spotify.search.exception.SpotifyException;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.io.IOException;

public class CreationAccessToken {

    private String clientId = "f6d89d8d397049678cbbf45f829dd85a";
    private String clientSecret = "cc7efec4579a45789125d76f29e16db0";
    private SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(clientId)
        .setClientSecret(clientSecret)
        .build();

    public String accessToken() {
        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
            throw new SpotifyException(e.getMessage());
        }

        return spotifyApi.getAccessToken();
    }
}
