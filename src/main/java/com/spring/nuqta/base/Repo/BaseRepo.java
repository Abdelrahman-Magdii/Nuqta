package com.spring.nuqta.base.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepo<T, ID extends Number> extends JpaRepository<T, ID> {


}
