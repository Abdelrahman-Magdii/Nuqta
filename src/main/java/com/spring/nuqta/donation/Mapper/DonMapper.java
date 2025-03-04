package com.spring.nuqta.donation.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.usermanagement.Mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface DonMapper extends BaseMapper<DonEntity, DonDto> {

    @Override
    @Mapping(target = "acceptedRequests", source = "acceptedRequests", qualifiedByName = "mapReqEntityToDto")
    DonDto map(DonEntity entity);

    @Override
    @Mapping(target = "acceptedRequests", source = "acceptedRequests", qualifiedByName = "mapReqDtoToEntity")
    DonEntity unMap(DonDto dto);

    /// **************** Map Req *************************//
    @Named("mapReqEntityToDto")
    default ReqDto mapReqEntityToDto(ReqEntity reqEntity) {
        if (reqEntity == null) {
            return null;
        }
        ReqDto reqDto = new ReqDto();
        reqDto.setId(reqEntity.getId());
        reqDto.setStatus(reqEntity.getStatus());
        reqDto.setAmount(reqEntity.getAmount());
        reqDto.setRequestDate(reqEntity.getRequestDate());
        reqDto.setBloodTypeNeeded(reqEntity.getBloodTypeNeeded());
        reqDto.setPaymentAvailable(reqEntity.getPaymentAvailable());
        reqDto.setUrgencyLevel(reqEntity.getUrgencyLevel());
        reqDto.setConservatism(reqEntity.getConservatism());
        reqDto.setCity(reqEntity.getCity());

        return reqDto;
    }

    @Named("mapReqDtoToEntity")
    default ReqEntity mapReqDtoToEntity(ReqDto reqDto) {
        if (reqDto == null) {
            return null;
        }
        ReqEntity req = new ReqEntity();
        req.setId(reqDto.getId());
        req.setStatus(reqDto.getStatus());
        req.setAmount(reqDto.getAmount());
        req.setRequestDate(reqDto.getRequestDate());
        req.setBloodTypeNeeded(reqDto.getBloodTypeNeeded());
        req.setPaymentAvailable(reqDto.getPaymentAvailable());
        req.setUrgencyLevel(reqDto.getUrgencyLevel());
        req.setConservatism(reqDto.getConservatism());
        req.setCity(reqDto.getCity());

        return req;
    }
}