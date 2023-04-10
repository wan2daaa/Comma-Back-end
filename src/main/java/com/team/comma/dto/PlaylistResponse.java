package com.team.comma.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class PlaylistResponse {

    private final Long playlistKey;

}