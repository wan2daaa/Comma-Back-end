package com.team.comma.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.comma.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public User findByEmail(String id);

}