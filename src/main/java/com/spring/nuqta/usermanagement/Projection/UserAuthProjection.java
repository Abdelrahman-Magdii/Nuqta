package com.spring.nuqta.usermanagement.Projection;

import com.spring.nuqta.enums.Scope;

public record UserAuthProjection(
        Long id,
        String username,
        String email,
        String password,
        Scope scope,
        boolean enabled
) {
    public UserAuthProjection(Long id, String username, String email, String password, Scope scope, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.scope = scope;
        this.enabled = enabled;
    }
}
