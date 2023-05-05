package com.team.comma.spotify.history.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryRequest {
    private String searchHistory;
}
