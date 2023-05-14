package com.team.comma.spotify.history.repository;


import com.team.comma.spotify.history.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom {

}
