package com.team.comma.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.comma.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String id);

}