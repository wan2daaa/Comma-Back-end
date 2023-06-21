package com.team.comma.follow.repository;

import com.team.comma.follow.domain.Following;
import com.team.comma.follow.dto.FollowingResponse;
import com.team.comma.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface FollowingRepositoryCustom {

    Optional<User> getFollowedMeUserByEmail(String toUserEmail , String fromUserEmail);

    Optional<User> getBlockedUser(String toUserEmail , String fromUserEmail);

    void blockFollowedUser(String toUserEmail , String fromUserEmail);

    void unblockFollowedUser(String toUserEmail , String fromUserEmail);

    List<FollowingResponse> getFollowingUserListByUser(User fromUser);
}
