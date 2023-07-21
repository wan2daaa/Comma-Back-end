package com.team.comma.spotify.recommend.dto;

import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.recommend.constant.RecommendType;
import com.team.comma.spotify.recommend.domain.Recommend;
import com.team.comma.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendRequest {

    private long recommendPlaylistId;
    private RecommendType recommendType;
    private String recommendToEmail;
    private String comment;

    public Recommend toRecommendEntity(User toUser, User fromUser, Playlist playlist){
        return Recommend.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .recommendType(recommendType)
                .comment(comment)
                .playlist(playlist)
                .build();
    }
}
