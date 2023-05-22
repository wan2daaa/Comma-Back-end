package com.team.comma.spotify.track.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.player.service.PlayerService;
import com.team.comma.spotify.track.service.TrackService;
import javax.security.auth.login.AccountException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tracks")
public class TrackController {

    private final TrackService trackService;

    private final PlayerService playerService;

    @PatchMapping("/alarms/{trackId}")
    public ResponseEntity<MessageResponse> modifyAlarmState(@PathVariable Long trackId) {
        return ResponseEntity.ok().body(trackService.updateAlarmFlag(trackId));
    }

    @GetMapping("/start/{trackId}")
    public ResponseEntity<MessageResponse> startAndResumePlayer(
        @PathVariable long trackId
    ) throws AccountException {
        return ResponseEntity.ok(playerService.startAndResumePlayer(trackId));
    }

}
