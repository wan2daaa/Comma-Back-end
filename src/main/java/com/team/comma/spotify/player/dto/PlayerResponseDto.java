package com.team.comma.spotify.player.dto;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerResponseDto {

    private final String spotifyAccessToken;

    private final String trackUri;

    public static PlayerResponseDto of(String spotifyAccessToken, String trackUri) {
        return new PlayerResponseDto(spotifyAccessToken, trackUri);
    }
}
