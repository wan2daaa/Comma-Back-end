package com.team.comma.spotify.player.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.player.service.PlayerService;
import javax.security.auth.login.AccountException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/start/{trackId}")
    public ResponseEntity<MessageResponse> startAndResumePlayer(
        @PathVariable long trackId
    ) throws AccountException {
        return ResponseEntity.ok(playerService.startAndResumePlayer(trackId));
    }

}
