package com.team.comma.spotify.recommend.repository;

import com.team.comma.spotify.recommend.domain.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {

}
