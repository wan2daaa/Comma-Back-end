package com.team.comma.repository;

import com.team.comma.entity.UserEntity;
import com.team.comma.entity.UserPlayList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MainRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findAllByUserKey(Integer userKey);
}
