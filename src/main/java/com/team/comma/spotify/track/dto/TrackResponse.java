package com.team.comma.spotify.track.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Schema(description = "Spotify 트랙 탐색")
public class TrackResponse {

    @Schema(description = "곡 ID")
    private String trackId;
    @Schema(description = "곡 제목")
    private String trackName;
    @Schema(description = "가수 명")
    private String artist;
    @Schema(description = "가수 ID")
    private String artistId;
    @Schema(description = "앨범 ID")
    private String albumId;
    @Schema(description = "1분 미리 듣기 주소")
    private String previewUrl;
    @Schema(description = "트랙 이미지")
    private Image[] images;
    @Schema(description = "인기도")
    private int popularity;
    @Schema(description = "발매일")
    private String releaseData;

    private String durationMinute;
    private String durationSecond;

    public static String MillToMinute(int ms) {
        SimpleDateFormat format = new SimpleDateFormat("mm");
        String date = (String) format.format(new Timestamp(ms));

        return date;
    }

    public static String MillToSecond(int ms) {
        SimpleDateFormat format = new SimpleDateFormat("ss");
        String date = (String) format.format(new Timestamp(ms));

        return date;
    }

    public static TrackResponse createTrackResponse(Track track) {
        return TrackResponse.builder()
                .trackId(track.getId())
                .images(track.getAlbum().getImages())
                .trackName(track.getName())
                .artist(track.getArtists()[0].getName())
                .artistId(track.getArtists()[0].getId())
                .albumId(track.getAlbum().getId())
                .previewUrl(track.getPreviewUrl())
                .popularity(track.getPopularity())
                .releaseData(track.getAlbum().getReleaseDate())
                .durationMinute(MillToMinute(track.getDurationMs()))
                .durationSecond(MillToSecond(track.getDurationMs()))
                .build();
    }
}
