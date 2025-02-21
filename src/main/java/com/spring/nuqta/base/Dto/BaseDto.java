package com.spring.nuqta.base.Dto;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public class BaseDto<ID extends Number> implements Serializable {

    private ID id;

}
