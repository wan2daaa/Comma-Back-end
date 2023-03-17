package com.team.comma.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.comma.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	public UserEntity findByEmail(String id);

}