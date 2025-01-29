package com.spring.nuqta.usermanagement.Services;

import com.spring.nuqta.authentication.Dto.AuthUserDto;
import com.spring.nuqta.authentication.Jwt.JwtUtilsUser;
import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServices extends BaseServices<UserEntity, Long> {

    private final UserRepo userRepository;
    private final JwtUtilsUser jwtUtils;
    private final PasswordEncoder passwordEncoder;

    private static void validateUserFields(UserEntity params) {
        if (Objects.isNull(params.getUsername())) {
            throw new GlobalException("Username is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getPassword())) {
            throw new GlobalException("Password is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getEmail())) {
            throw new GlobalException("Email is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getPhoneNumber())) {
            throw new GlobalException("Mobile phone number is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getScope())) {
            throw new GlobalException("Scope is required.", HttpStatus.BAD_REQUEST);
        }

        if (!(Scope.USER.equals(params.getScope()))) {
            throw new GlobalException("Invalid scope. Scope must be 'USER'.", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(params.getDonation().getLocation())) {
            throw new GlobalException("Location is required.", HttpStatus.BAD_REQUEST);
        }
    }

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
    public UserEntity update(UserEntity entity) throws GlobalException {
        if (entity == null || entity.getId() == null) {
            throw new GlobalException("User ID cannot be null", HttpStatus.BAD_REQUEST);
        }
        UserEntity existingUser = super.findById(entity.getId());
        if (existingUser == null) {
            throw new GlobalException("User not found with ID: " + entity.getId(), HttpStatus.NOT_FOUND);
        }

        if (existingUser.getDonation().getId() == null || !existingUser.getDonation().getId().equals(entity.getDonation().getId())) {
            throw new GlobalException("Donation not found with ID: " + entity.getDonation().getId(), HttpStatus.NOT_FOUND);
        }

        existingUser.setId(entity.getId());
        existingUser.setUsername(entity.getUsername());
//        existingUser.setEmail(entity.getEmail());
//        existingUser.setPassword(passwordEncoder.encode(entity.getPassword()));
        existingUser.setPhoneNumber(entity.getPhoneNumber());
        existingUser.setScope(entity.getScope());
        existingUser.setDonation(entity.getDonation());

        return super.update(existingUser);
    }

    @Override
    public void deleteById(Long id) throws GlobalException {
        UserEntity user = super.findById(id);
        if (user == null) {
            throw new GlobalException("User not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        super.deleteById(id);
    }

    @Transactional
    public AuthUserDto create(UserEntity entity) {
        validateUserFields(entity);

        Optional<UserEntity> existingUser =
                userRepository.findByUsernameOrEmail(entity.getUsername(), entity.getEmail());

        if (existingUser.isPresent()) {
            throw new GlobalException("Username and email is exist", HttpStatus.BAD_REQUEST);
        }

        UserEntity userCreation = new UserEntity(entity.getUsername(), entity.getEmail(),
                passwordEncoder.encode(entity.getPassword()), entity.getBirthDate(),
                entity.getPhoneNumber(), entity.getScope(), entity.getDonation());

        userCreation = userRepository.save(userCreation);


        String token = jwtUtils.generateToken(userCreation);
        AuthUserDto userDto = new AuthUserDto(userCreation.getId(), token,
                String.valueOf(jwtUtils.getExpireAt(token)),
                userCreation.getScope());


        return userDto;
    }

}