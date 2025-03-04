package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Mapper.DonMapper;
import com.spring.nuqta.usermanagement.Dto.UserInsertDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {DonMapper.class})
public interface UserInsertMapper extends BaseMapper<UserEntity, UserInsertDto> {

}
