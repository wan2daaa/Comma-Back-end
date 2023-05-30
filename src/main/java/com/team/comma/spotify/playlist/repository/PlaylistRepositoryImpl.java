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

    @Override
    public long updateAlarmFlag(long id, boolean alarmFlag) {
        return queryFactory.update(playlist)
                .set(playlist.alarmFlag, alarmFlag)
                .where(playlist.id.eq(id))
                .execute();
    }

    @Override
    public long deletePlaylist(long id) {
        return queryFactory.update(playlist)
                .set(playlist.delFlag,true)
                .where(playlist.id.eq(id))
                .execute();
    }
}
