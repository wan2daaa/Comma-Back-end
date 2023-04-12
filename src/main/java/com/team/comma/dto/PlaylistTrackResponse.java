package com.team.comma.dto;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor
public class PlaylistTrackResponse {
    private final Long id;
    private final String trackTitle;
    private final Integer durationMs;
    private final String artistName;
    private final String albumName;
    private final String albumImageUrl;
    private final Boolean alarmFlag;
}
