package com.team.comma.follow.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FollowingRequest {
    private String toUserEmail;
}
