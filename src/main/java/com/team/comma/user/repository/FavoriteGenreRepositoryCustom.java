package com.team.comma.user.repository;

import com.team.comma.user.domain.User;

import java.util.List;

public interface FavoriteGenreRepositoryCustom {
    List<String> findByGenreNameList(User user);
}
