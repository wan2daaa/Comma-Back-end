package com.team.comma.spotify.playlist.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.dto.PlaylistRequest;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackRequest;
import com.team.comma.spotify.playlist.service.PlaylistService;
import com.team.comma.spotify.playlist.service.PlaylistTrackService;
import jakarta.validation.Valid;
import java.util.List;
import javax.security.auth.login.AccountException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    private final PlaylistTrackService playlistTrackService;

    @GetMapping("/userPlaylist")
    public ResponseEntity<List<PlaylistResponse>> getUserPlaylist(
        @RequestHeader("email") final String email) {
        return ResponseEntity.ok(playlistService.getPlaylistResponse(email));
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
