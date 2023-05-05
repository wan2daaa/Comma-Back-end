package com.team.comma.spotify.history.repository;

import com.team.comma.spotify.history.dto.HistoryResponse;
import com.team.comma.user.domain.User;

import java.util.List;

public interface HistoryRepositoryCustom {
    List<HistoryResponse> getHistoryListByUserEmail(String userEmail);
    void deleteHistoryById(long id);

    void deleteAllHistoryByUser(User user);
}
