package com.team.comma.spotify.playlist.repository;

import static com.team.comma.spotify.playlist.domain.QPlaylist.playlist;
import static com.team.comma.spotify.track.domain.QTrack.track;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaylistRepositoryImpl implements PlaylistRepositoryCustom {


    private final JPAQueryFactory queryFactory;


    @Override
    public int getTotalDurationTimeMsWithPlaylistId(Long playlistId) {
        return queryFactory.select(track.durationTimeMs.sum().coalesce(0))
            .from(playlist)
            .innerJoin(track).fetchJoin()
            .where(playlist.id.eq(playlistId))
            .fetchOne();

    }

    @Override
    public int findMaxListSequence() {
        return queryFactory.select(playlist.listSequence.max().coalesce(0))
            .from(playlist)
            .fetchOne();
    }
}
