package com.spring.nuqta.authentication.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.nuqta.enums.Scope;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthUserDto {


    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("token")
    private String accessToken;
    @JsonProperty("expire_at")
    private String expireAt;
    private Scope scope;

    public AuthUserDto(Long userId, String accessToken, String expireAt, Scope scope) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.expireAt = expireAt;
        this.scope = scope;
    }

}
