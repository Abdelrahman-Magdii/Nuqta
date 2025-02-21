package com.spring.nuqta.organization.Projection;

import com.spring.nuqta.enums.Scope;

public record OrgAuthProjection(
        Long id,
        String email,
        String password,
        String licenseNumber,
        Scope scope,
        boolean enabled
) {
}
