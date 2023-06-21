package com.team.comma.spotify.playlist.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author: wan2daaa
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistTrackRequest {

    @NotNull(message = "트랙IdList 는 null 일 수 없습니다.")
    private Set<Long> trackIdList;

    @NotNull(message = "플리Id 는 null 일 수 없습니다.")
    private long playlistId;
}
