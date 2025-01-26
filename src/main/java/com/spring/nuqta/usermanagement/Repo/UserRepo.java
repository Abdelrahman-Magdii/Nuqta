package com.spring.nuqta.usermanagement.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepo extends BaseRepo<UserEntity, Long> {


    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsernameOrEmail(String username, String email);
}
