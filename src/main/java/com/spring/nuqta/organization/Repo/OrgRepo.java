package com.spring.nuqta.organization.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.organization.Entity.OrgEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepo extends BaseRepo <OrgEntity,Long> {

}
