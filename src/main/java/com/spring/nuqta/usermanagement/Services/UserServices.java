package com.spring.nuqta.usermanagement.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServices extends BaseServices<UserEntity, Long> {


    @Override
    public List<UserEntity> findAll() throws GlobalException {
        List<UserEntity> users = super.findAll();
        if (users.isEmpty()) {
            throw new GlobalException("No users found", HttpStatus.NOT_FOUND);
        }
        return users;
    }


    @Override
    public UserEntity findById(Long id) throws GlobalException {
        UserEntity user = super.findById(id);
        if (user == null) {
            throw new GlobalException("User not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        return user;
    }


    @Override
    public UserEntity insert(UserEntity entity) throws GlobalException {
        if (entity == null || entity.getUsername() == null || entity.getUsername().isEmpty()) {
            throw new GlobalException("Username cannot be empty", HttpStatus.BAD_REQUEST);
        }
        return super.insert(entity);
    }


    @Override
    public UserEntity update(UserEntity entity) throws GlobalException {
        if (entity == null || entity.getId() == null) {
            throw new GlobalException("User ID cannot be null", HttpStatus.BAD_REQUEST);
        }
        UserEntity existingUser = super.findById(entity.getId());
        if (existingUser == null) {
            throw new GlobalException("User not found with ID: " + entity.getId(), HttpStatus.NOT_FOUND);
        }
        return super.update(entity);
    }


    @Override
    public void deleteById(Long id) throws GlobalException {
        UserEntity user = super.findById(id);
        if (user == null) {
            throw new GlobalException("User not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        super.deleteById(id);
    }
}