package com.team.comma.spotify.track.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Spotify 트랙 탐색")
public class TrackResponse {

    @Schema(description = "곡 ID")
    private String id;
    @Schema(description = "곡 제목")
    private String name;
    @Schema(description = "Spotify uri 에서 Track의 주소")
    private String uri;
    @Schema(description = "가수 명")
    private ArtistSimplified[] artists;
    @Schema(description = "1분 미리 듣기 주소")
    private String previewUrl;
    @Schema(description = "트랙의 재생 주소 ( 토큰 필요 ) ")
    private String href;

    public static TrackResponse createTrackResponse(Track track) {
        return TrackResponse.builder()
                .id(track.getId())
                .name(track.getName())
                .uri(track.getUri())
                .artists(track.getArtists())
                .previewUrl(track.getPreviewUrl())
                .href(track.getHref())
                .build();
    }
}
