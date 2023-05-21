package com.team.comma.follow.repository;

import com.team.comma.follow.domain.Following;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowingRepository extends JpaRepository<Following, Long> , FollowingRepositoryCustom {

}
