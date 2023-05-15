package com.team.comma.spotify.playlist.repository;

import static com.team.comma.spotify.playlist.domain.QPlaylistTrack.playlistTrack;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class PlaylistTrackRepositoryImpl implements PlaylistTrackRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    @Transactional
    public long changeAlarmFlagWithTrackId(Long trackId) {
        return queryFactory.update(playlistTrack)
            .set(playlistTrack.trackAlarmFlag, playlistTrack.trackAlarmFlag.not())
            .where(playlistTrack.track.id.eq(trackId))
            .execute();
    }

}
