package com.spring.nuqta.request.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.request.Entity.ReqEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ReqRepo extends BaseRepo<ReqEntity, Long> {

    @Modifying
    @Query("DELETE FROM ReqEntity r WHERE r.id = :id")
    void hardDeleteById(@Param("id") Long id);

}
