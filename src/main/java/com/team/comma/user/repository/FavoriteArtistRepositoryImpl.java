package com.team.comma.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.user.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.team.comma.user.domain.QFavoriteArtist.favoriteArtist;

@RequiredArgsConstructor
public class FavoriteArtistRepositoryImpl implements FavoriteArtistRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findArtistListByUser(User user) {
        return queryFactory.select(favoriteArtist.artistName)
                .from(favoriteArtist)
                .where(favoriteArtist.user.eq(user))
                .fetch();
    }
}
