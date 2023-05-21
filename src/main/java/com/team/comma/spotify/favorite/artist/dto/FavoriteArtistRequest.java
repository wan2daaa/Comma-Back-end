package com.team.comma.spotify.favorite.artist.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FavoriteArtistRequest {
    private String artistName;
}
