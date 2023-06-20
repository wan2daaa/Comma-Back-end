package com.team.comma.follow.dto;

import com.team.comma.follow.domain.Following;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FollowingResponse {

    private final long followingId;
    private final String fromUserEmail;
    private final String toUserEmail;

    private FollowingResponse(Following following) {
        this.followingId = following.getId();
        this.fromUserEmail = following.getUserFrom().getEmail();
        this.toUserEmail = following.getUserTo().getEmail();
    }

    public static FollowingResponse of(Following following){
        return new FollowingResponse(following);
    }
}
