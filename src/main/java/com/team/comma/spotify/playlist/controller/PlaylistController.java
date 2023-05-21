package com.team.comma.spotify.playlist.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.dto.PlaylistRequest;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.playlist.dto.PlaylistUpdateRequest;
import com.team.comma.spotify.playlist.exception.PlaylistException;
import com.team.comma.spotify.playlist.service.PlaylistService;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playlist")
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping
    public ResponseEntity<List<PlaylistResponse>> getUserPlaylist(
        @CookieValue final String accessToken) throws AccountException {
        return ResponseEntity.ok().body(playlistService.getPlaylists(accessToken));
    }

    @PatchMapping("/alert")
    public ResponseEntity<MessageResponse> modifyAlarmState(
        @RequestBody final PlaylistRequest request) throws PlaylistException {
        return ResponseEntity.ok()
            .body(playlistService.updateAlarmFlag(request.getPlaylistId(), request.isAlarmFlag()));
    }

    @GetMapping("/all-duration-time/{id}")
    public ResponseEntity<MessageResponse> getPlaylistAllDurationTime(
        @PathVariable("id") final Long id
    ) {
        return ResponseEntity.ok(
            playlistService.getTotalDurationTimeMsByPlaylist(id));
    }

    @PatchMapping
    public ResponseEntity<MessageResponse> patchPlaylist(
        @RequestBody final PlaylistUpdateRequest playlistUpdateRequest
    ) {
        return ResponseEntity.ok(
            playlistService.updatePlaylist(playlistUpdateRequest)
        );
    }


}
