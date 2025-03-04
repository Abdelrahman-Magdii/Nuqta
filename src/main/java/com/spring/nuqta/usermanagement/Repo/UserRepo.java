package com.spring.nuqta.usermanagement.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Projection.UserAuthProjection;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepo extends BaseRepo<UserEntity, Long> {

    List<UserEntity> findAllByEnabledTrue();

    Optional<UserEntity> findByIdAndEnabledTrue(Long id);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    Optional<UserAuthProjection> findUserAuthProjectionByUsername(String username);

    Optional<UserAuthProjection> findUserAuthProjectionByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    boolean existsByUsernameAndIdNot(String username, Long id);

    Optional<UserAuthProjection> findUserAuthProjectionByUsernameOrEmail(String username, String email);
}
