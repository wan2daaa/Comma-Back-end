package com.team.comma.spotify.playlist.controller;

import com.team.comma.common.dto.MessageResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @Operation(summary = "사용자 플레이리스트 조회", description = "사용자 이메일로 플레이리스트 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = PlaylistResponse.class)))
    })
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

}
