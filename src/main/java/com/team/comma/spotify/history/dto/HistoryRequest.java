package com.team.comma.spotify.history.dto;

import lombok.*;

@Builder
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class HistoryRequest {
    private String searchHistory;
}
