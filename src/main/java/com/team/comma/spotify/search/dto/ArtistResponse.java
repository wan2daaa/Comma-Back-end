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
public class ArtistResponse {

    private String artistId;
    private String artistName;
    private String[] genres;
    private Image[] images;
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
