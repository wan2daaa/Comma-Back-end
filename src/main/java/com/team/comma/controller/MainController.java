package com.team.comma.controller;

import com.team.comma.dto.PlaylistResponse;
import com.team.comma.service.MainService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainController {
    private final MainService mainService;

    @GetMapping("/main/playlist")
    public ResponseEntity<List<PlaylistResponse>> getUserPlaylist(
            @RequestHeader("email") final String email) {
        return ResponseEntity.ok(mainService.getUserPlaylist(email));
    }
}
