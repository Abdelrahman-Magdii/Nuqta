package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Mapper.DonResponseUserMapper;
import com.spring.nuqta.request.Mapper.AddReqMapper;
import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {DonResponseUserMapper.class, AddReqMapper.class})
public interface UserMapper extends BaseMapper<UserEntity, UserDto> {

    @Override
    @Mapping(target = "age", expression = "java(com.spring.nuqta.usermanagement.Mapper.DateUtils.calculateAgeFromBirthDate(entity.getBirthDate()))")
    UserDto map(UserEntity entity);

    @Override
    @Mapping(target = "birthDate", expression = "java(com.spring.nuqta.usermanagement.Mapper.DateUtils.calculateBirthDateFromAge(dto.getAge()))")
    UserEntity unMap(UserDto dto);

    @AfterMapping
    default void handleConfirmDonate(@MappingTarget UserDto dto) {
        if (dto.getDonation() != null && dto.getDonation().getConfirmDonate() == null) {
            dto.getDonation().setConfirmDonate(false);
        }
    }
}
