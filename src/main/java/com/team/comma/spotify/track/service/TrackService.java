package com.team.comma.spotify.track.service;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackArtistResponse;
import com.team.comma.spotify.playlist.repository.PlaylistTrackRepository;
import com.team.comma.spotify.track.domain.TrackArtist;
import com.team.comma.spotify.track.repository.TrackRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackService {

    private final PlaylistTrackRepository playlistTrackRepository;
    private final TrackRepository trackRepository;


    public List<PlaylistTrackArtistResponse> getTrackArtistResponseList(final List<TrackArtist> artists){
        List<PlaylistTrackArtistResponse> result = new ArrayList<>();
        for (TrackArtist artist : artists){
            result.add(PlaylistTrackArtistResponse.of(artist));
        }
        return result;
    }

    public MessageResponse updateAlarmFlag(Long trackId) {

        validateIsTrackExists(trackId);

        long updatedCount = playlistTrackRepository.changeAlarmFlagWithTrackId(trackId);

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    private void validateIsTrackExists(Long trackId) {
        trackRepository.findById(trackId)
            .orElseThrow(EntityNotFoundException::new);
    }

}
