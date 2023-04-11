package com.team.comma.dto;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class PlaylistTrackResponse {
    private Long id;
    private String trackTitle;
    private Integer durationMs;
    private String artistName;
    private String albumName;
    private String albumImageUrl;
    private Boolean alarmFlag;
}
