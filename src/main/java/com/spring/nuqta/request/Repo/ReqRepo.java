package com.spring.nuqta.request.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.request.Entity.ReqEntity;
import org.springframework.stereotype.Repository;


@Repository
public interface ReqRepo extends BaseRepo<ReqEntity, Long> {
}
