package com.team.comma.spotify.search.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Spotify 아티스트 탐색")
public class ArtistResponse {

    @Schema(description = "아티스트 ID")
    private String artistId;
    @Schema(description = "가수 명")
    private String artistName;
    @Schema(description = "가수의 장르")
    private String[] genres;
    @Schema(description = "아티스트 이미지와 크기")
    private Image[] images;
    @Schema(description = "인기도")
    private int popularity;

    public static ArtistResponse createArtistResponse(Artist artist) {
        return ArtistResponse.builder()
                .artistId(artist.getId())
                .artistName(artist.getName())
                .genres(artist.getGenres())
                .images(artist.getImages())
                .popularity(artist.getPopularity())
                .build();
    }
}
