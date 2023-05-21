package com.team.comma.follow.repository;

import com.team.comma.user.domain.User;

import java.util.Optional;

public interface FollowingRepositoryCustom {

    Optional<User> getFollowedMeUserByEmail(String toUserEmail , String fromUserEmail);

    Optional<User> getBlockedUser(String toUserEmail , String fromUserEmail);

    void blockFollowedUser(String toUserEmail , String fromUserEmail);

    void unblockFollowedUser(String toUserEmail , String fromUserEmail);

}
