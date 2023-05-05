package com.team.comma.spotify.history.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoryResponse {
    private final long id;
    private final String searchHistory;

}
