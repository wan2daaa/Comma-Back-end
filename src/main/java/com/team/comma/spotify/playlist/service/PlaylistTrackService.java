package com.team.comma.spotify.playlist.service;

import static com.team.comma.common.constant.ResponseCodeTest.REQUEST_SUCCESS;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistTrackService {

    private final PlaylistTrackRepository playlistTrackRepository;

    public MessageResponse disconnectPlaylistAndTrack(Set<Long> trackIdList, Long playlistId) {
        int deleteCount = 0;

        for (Long trackId : trackIdList) {
            playlistTrackRepository.findByTrackIdAndPlaylistId(trackId, playlistId)
                .orElseThrow(() -> new EntityNotFoundException("해당 플레이리스트에 존재하지 않는 트랙입니다."));

            deleteCount += playlistTrackRepository.
                deletePlaylistTrackByTrackIdAndPlaylistId(trackId, playlistId);
        }
        return MessageResponse.of(
            REQUEST_SUCCESS.getCode(),
            REQUEST_SUCCESS.getMessage(),
            deleteCount
        );
    }
}

