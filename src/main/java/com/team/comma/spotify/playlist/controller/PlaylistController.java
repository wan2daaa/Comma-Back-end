package com.team.comma.spotify.playlist.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.dto.PlaylistRequest;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackRequest;
import com.team.comma.spotify.playlist.dto.PlaylistUpdateRequest;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.playlist.service.PlaylistService;
import com.team.comma.spotify.playlist.service.PlaylistTrackService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    private final PlaylistTrackService playlistTrackService;

    @GetMapping("/userPlaylist")
    public ResponseEntity<List<PlaylistResponse>> getUserPlaylist(
        @CookieValue final String accessToken) {
        return ResponseEntity.ok().body(playlistService.getPlaylist(accessToken));
    }

    @PatchMapping("/playlist/alert")
    public ResponseEntity<MessageResponse> modifyAlarmState(
        @RequestBody final PlaylistRequest request) throws PlaylistException {
        return ResponseEntity.ok()
            .body(playlistService.updateAlarmFlag(request.getPlaylistId(), request.isAlarmFlag()));
    }

    @GetMapping("/playlist/all-duration-time/{id}")
    public ResponseEntity<MessageResponse> getPlaylistAllDurationTime(
        @PathVariable("id") final Long id
    ) {
        return ResponseEntity.ok(
            playlistService.getTotalDurationTimeMsByPlaylist(id));
    }

    @DeleteMapping("/playlist-track")
    public ResponseEntity<MessageResponse> disconnectPlaylistAndTrack
        (
            @Valid @RequestBody final PlaylistTrackRequest playlistTrackRequest
        ) {
        return ResponseEntity.ok(
            playlistTrackService.disconnectPlaylistAndTrack(
                playlistTrackRequest.getTrackIdList(),
                playlistTrackRequest.getPlaylistId()
            )
        );
    }

    @PostMapping("/playlist")
    public ResponseEntity<MessageResponse> createPlaylist(
        @CookieValue(value = "accessToken") String accessToken,
        @RequestBody final PlaylistUpdateRequest playlistUpdateRequest
    ) throws Exception {
        return ResponseEntity.ok(
            playlistService.createPlaylist(playlistUpdateRequest, accessToken)
        );
    }

    @PatchMapping("/playlist")
    public ResponseEntity<MessageResponse> patchPlaylist(
        @RequestBody final PlaylistUpdateRequest playlistUpdateRequest
    ) {
        return ResponseEntity.ok(
            playlistService.updatePlaylist(playlistUpdateRequest)
        );
    }


}
