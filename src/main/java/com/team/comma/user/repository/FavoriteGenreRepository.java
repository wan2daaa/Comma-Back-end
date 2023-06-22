package com.team.comma.user.repository;

import com.team.comma.user.domain.FavoriteGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteGenreRepository extends JpaRepository<FavoriteGenre, Long> , FavoriteGenreRepositoryCustom {

}
