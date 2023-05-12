package com.team.comma.user.repository;

import com.team.comma.user.domain.FavoriteArtist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteArtistRepository extends JpaRepository<FavoriteArtist, Long> , FavoriteArtistRepositoryCustom {

}
