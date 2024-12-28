package com.spring.nuqta.usermanagement.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepo extends BaseRepo<UserEntity, Long> {


}
