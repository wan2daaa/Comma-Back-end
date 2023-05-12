package com.team.comma.user.repository;

import com.team.comma.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {

    List<User> searchUserByUserNameAndNickName(String name);
    Optional<User> findByEmail(String email);

}
