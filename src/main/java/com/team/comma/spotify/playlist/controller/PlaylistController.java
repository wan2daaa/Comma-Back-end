package com.team.comma.spotify.playlist.controller;

import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping("/playlist")
    private final PlaylistTrackService playlistTrackService;

    @GetMapping("/userPlaylist")
    public ResponseEntity<List<PlaylistResponse>> getUserPlaylist(
            @CookieValue final String accessToken) {
        return ResponseEntity.ok().body(playlistService.getPlaylist(accessToken));
    }

    @PatchMapping("/playlist/alert")
    public ResponseEntity<MessageResponse> modifyAlarmState(
            @RequestBody final PlaylistRequest request) throws PlaylistException {
        return ResponseEntity.ok().body(playlistService.updateAlarmFlag(request.getPlaylistId(), request.isAlarmFlag()));
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
        @RequestBody final PlaylistRequest playlistRequest
    ) throws Exception {
        return ResponseEntity.ok(
            playlistService.createPlaylist(playlistRequest, accessToken)
        );
    }

    @PatchMapping("/playlist")
    public ResponseEntity<MessageResponse> patchPlaylist(
        @RequestBody final PlaylistRequest playlistRequest
    ) {
        return ResponseEntity.ok(
            playlistService.updatePlaylist(playlistRequest)
        );
    }


}
