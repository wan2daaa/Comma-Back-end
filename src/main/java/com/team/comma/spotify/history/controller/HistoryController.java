package com.team.comma.spotify.history.controller;

import com.team.comma.spotify.history.dto.HistoryRequest;
import com.team.comma.spotify.history.service.HistoryService;
import com.team.comma.spotify.search.dto.RequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spotify")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/history")
    public RequestResponse getHistoryListByToken(@CookieValue("accessToken") String token) throws AccountException {
        return historyService.getHistoryList(token);
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity deleteUserHistory(@PathVariable long id) {
        historyService.deleteHistory(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/all-history")
    public ResponseEntity deleteUserAllHistory(@CookieValue("accessToken") String token) throws AccountException {
        historyService.deleteAllHistory(token);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/history")
    public ResponseEntity addHistory(@RequestBody HistoryRequest historyRequest
            , @CookieValue("accessToken") String token) throws AccountException {
        historyService.addHistory(historyRequest , token);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
