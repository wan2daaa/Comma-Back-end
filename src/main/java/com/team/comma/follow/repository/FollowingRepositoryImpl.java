package com.team.comma.follow.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.follow.domain.Following;
import com.team.comma.follow.dto.FollowingResponse;
import com.team.comma.user.domain.QUser;
import com.team.comma.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.team.comma.follow.domain.QFollowing.following;

@RequiredArgsConstructor
public class FollowingRepositoryImpl implements FollowingRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    QUser user1 = new QUser("user1");
    QUser user2 = new QUser("user2");

    @Override
    public Optional<User> getFollowedMeUserByEmail(String toUserEmail , String fromUserEmail) {
        User result = queryFactory.select(following.userTo).from(following)
                .innerJoin(following.userTo , user1).on(user1.email.eq(toUserEmail))
                .innerJoin(following.userFrom , user2).on(user2.email.eq(fromUserEmail))
                .where(following.blockFlag.eq(false))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<User> getBlockedUser(String toUserEmail, String fromUserEmail) {
        User result = queryFactory.select(following.userTo).from(following)
                .innerJoin(following.userTo , user1).on(user1.email.eq(toUserEmail))
                .innerJoin(following.userFrom , user2).on(user2.email.eq(fromUserEmail))
                .where(following.blockFlag.eq(true))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public void blockFollowedUser(String toUserEmail, String fromUserEmail) {
        queryFactory.update(following)
                .set(following.blockFlag , true)
                .where(following.id.eq(
                        JPAExpressions.select(following.id).from(following)
                                .innerJoin(following.userTo , user1).on(user1.email.eq(toUserEmail))
                                .innerJoin(following.userFrom , user2).on(user2.email.eq(fromUserEmail))
                ))
                .execute();
    }

    @Override
    @Transactional
    public void unblockFollowedUser(String toUserEmail, String fromUserEmail) {
        queryFactory.update(following)
                .set(following.blockFlag , false)
                .where(following.id.eq(
                        JPAExpressions.select(following.id).from(following)
                                .innerJoin(following.userTo , user1).on(user1.email.eq(toUserEmail))
                                .innerJoin(following.userFrom , user2).on(user2.email.eq(fromUserEmail))
                ))
                .execute();
    }

    @Override
    public List<FollowingResponse> getFollowingUserListByUser(User fromUser) {
        return queryFactory.select(
                                Projections.constructor(
                                    FollowingResponse.class,
                                    following.id,
                                    following.userFrom.email,
                                    following.userTo.email))
                            .from(following)
                            .where(following.userFrom.eq(fromUser)
                                    .and(following.blockFlag.eq(false)))
                            .fetch();
    }

}
