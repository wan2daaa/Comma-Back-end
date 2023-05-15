package com.team.comma.spotify.player.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.player.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/start/{trackId}")
    public ResponseEntity<MessageResponse> startAndResumePlayer(
        @PathVariable long trackId
    ) {
        return ResponseEntity.ok(
            playerService.startAndResumePlayer(trackId)
        );
    }

    @GetMapping("/pause")
    public ResponseEntity<MessageResponse> pausePlayer() {
        return ResponseEntity.ok(
            playerService.pausePlayer()
        );
    }

}
