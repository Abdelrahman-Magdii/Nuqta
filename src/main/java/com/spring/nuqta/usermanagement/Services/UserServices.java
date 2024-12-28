package com.spring.nuqta.usermanagement.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public class UserServices extends BaseServices<UserEntity, Long> {

    @Override
    public List<UserEntity> findAll() {
        if (super.findAll().isEmpty()) {
            throw new NoSuchElementException("No Found Users");
        }
        return super.findAll();
    }

    @Override
    public UserEntity findById(Long aLong) {
        if (super.findById(aLong) == null) {
            throw new NoSuchElementException("No Found User that id :" + aLong);
        }
        return super.findById(aLong);
    }

    @Override
    public UserEntity insert(UserEntity entity) {

        return super.insert(entity);
    }

    @Override
    public UserEntity update(UserEntity entity) {
        return super.update(entity);
    }

    @Override
    public void deleteById(Long aLong) {
        if (super.findById(aLong) == null) {
            throw new NoSuchElementException("No Found User that id :" + aLong);
        }
        super.deleteById(aLong);
    }
}
