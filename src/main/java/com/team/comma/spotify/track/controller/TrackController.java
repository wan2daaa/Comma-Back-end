package com.team.comma.spotify.track.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.track.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/track")
public class TrackController {

    private final TrackService trackService;

    @PatchMapping("/alarm/{trackId}")
    public ResponseEntity<MessageResponse> modifyAlarmState(@PathVariable Long trackId) {
        return ResponseEntity.ok().body(trackService.updateAlarmFlag(trackId));
    }


}
