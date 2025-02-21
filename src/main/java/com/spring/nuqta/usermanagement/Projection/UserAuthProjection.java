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
}
