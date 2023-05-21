package com.team.comma.spotify.favorite.artist.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.comma.spotify.favorite.artist.domain.FavoriteArtist;
import com.team.comma.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.team.comma.user.domain.QUser;

import java.util.List;
import java.util.Optional;

import static com.team.comma.spotify.favorite.artist.domain.QFavoriteArtist.favoriteArtist;


@RequiredArgsConstructor
public class FavoriteArtistRepositoryImpl implements FavoriteArtistRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    QUser qUser = new QUser("user");

    @Override
    public List<String> findFavoriteArtistListByUser(User user) {
        return queryFactory.select(favoriteArtist.artistName)
                .from(favoriteArtist)
                .where(favoriteArtist.user.eq(user))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteByUser(User user , String artistName) {
        queryFactory.delete(favoriteArtist)
                .where(favoriteArtist.id.eq(
                        JPAExpressions.select(favoriteArtist.id).from(favoriteArtist)
                                .innerJoin(favoriteArtist.user , qUser).on(qUser.eq(user))
                                .where(favoriteArtist.artistName.eq(artistName))
                ))
                .execute();
    }

    @Override
    public Optional<FavoriteArtist> findFavoriteArtistByUser(User user, String artistName) {
        FavoriteArtist result = queryFactory.select(favoriteArtist).from(favoriteArtist)
                .innerJoin(favoriteArtist.user , qUser).on(qUser.eq(user))
                .where(favoriteArtist.artistName.eq(artistName))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
