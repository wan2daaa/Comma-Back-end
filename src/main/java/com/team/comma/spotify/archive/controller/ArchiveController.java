package com.team.comma.spotify.archive.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.archive.dto.ArchiveRequest;
import com.team.comma.spotify.archive.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/archives")
public class ArchiveController {

    private final ArchiveService archiveService;

    @PostMapping
    public ResponseEntity<MessageResponse> addArchive(@CookieValue String accessToken
            , @RequestBody ArchiveRequest archiveRequest) throws AccountException {
        return ResponseEntity.status(HttpStatus.CREATED).body(archiveService.addArchive(accessToken , archiveRequest));
    }
}
