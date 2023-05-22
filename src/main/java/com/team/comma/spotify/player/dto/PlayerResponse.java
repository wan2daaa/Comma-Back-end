package com.team.comma.spotify.player.dto;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerResponse {

    private final String spotifyAccessToken;

    private final String trackUri;

    public static PlayerResponse of(String spotifyAccessToken, String trackUri) {
        return new PlayerResponse(spotifyAccessToken, trackUri);
    }
}
