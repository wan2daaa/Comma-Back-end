package com.team.comma.repository;

import com.team.comma.dto.PlaylistResponse;
import com.team.comma.entity.UserEntity;
import com.team.comma.entity.UserPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MainRepository extends JpaRepository<UserPlaylist, Long> {

    List<PlaylistResponse> findAllByUserEntity_Email(String email);
}
