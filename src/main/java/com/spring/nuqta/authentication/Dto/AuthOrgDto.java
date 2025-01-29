package com.spring.nuqta.authentication.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.nuqta.enums.Scope;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthOrgDto {

    @JsonProperty("org_id")
    private Long orgId;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expire_at")
    private String expireAt;
    @JsonProperty("scope")
    private Scope scope;

    public AuthOrgDto(Long orgId, String accessToken, String expireAt, Scope scope) {
        this.orgId = orgId;
        this.accessToken = accessToken;
        this.expireAt = expireAt;
        this.scope = scope;
    }
}
