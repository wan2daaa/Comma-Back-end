package com.team.comma.spotify.recommend.controller;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.recommend.dto.RecommendRequest;
import com.team.comma.spotify.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    @PostMapping
    public ResponseEntity<MessageResponse> recommendPlaylist(
            @CookieValue final String accessToken,
            @RequestBody final RecommendRequest recommendRequest
            ) throws AccountException {
        return ResponseEntity.ok().body(
                recommendService.addRecommend(accessToken, recommendRequest));
    }

}
