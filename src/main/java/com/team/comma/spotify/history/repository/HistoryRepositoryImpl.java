package com.team.comma.spotify.history.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.spotify.history.dto.HistoryResponse;
import com.team.comma.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.spotify.history.domain.QHistory.history;
import static com.team.comma.user.domain.QUser.user;

@RequiredArgsConstructor
public class HistoryRepositoryImpl implements HistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<HistoryResponse> getHistoryListByUserEmail(String userEmail) {
        return queryFactory.select(Projections.constructor(HistoryResponse.class, history.id, history.searchHistory))
                .from(history)
                .innerJoin(history.user, user).on(user.email.eq(userEmail))
                .where(history.delFlag.eq(false))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteHistoryById(long id) {
        queryFactory.update(history)
                .set(history.delFlag, true)
                .where(history.id.in(id))
                .execute();
    }

    @Override
    @Transactional
    public void deleteAllHistoryByUser(User user) {
        queryFactory.update(history)
                .set(history.delFlag, true)
                .where(history.user.in(user))
                .execute();
    }
}

