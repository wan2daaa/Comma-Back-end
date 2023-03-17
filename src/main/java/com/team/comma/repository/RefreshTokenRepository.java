package com.team.comma.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team.comma.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
	
	@Query(value = "SELECT p from RefreshToken p where p.keyEmail = :userEmail")
	RefreshToken existsByKeyEmail(@Param("userEmail") String userEmail);
	
	void deleteByKeyEmail(String userEmail);
}
