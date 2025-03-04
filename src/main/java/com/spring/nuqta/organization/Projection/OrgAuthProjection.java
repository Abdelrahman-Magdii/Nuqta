package com.spring.nuqta.organization.Projection;

import com.spring.nuqta.enums.Scope;

public record OrgAuthProjection(
        Long id,
        String email,
        String password,
        String orgName,
        Scope scope,
        boolean enabled,
        String licenseNumber
) {
    // Custom constructor with validation
    public OrgAuthProjection(Long id, String email, String password, String orgName, Scope scope, boolean enabled, String licenseNumber) {
        // Assign values to record components
        this.id = id;
        this.email = email;
        this.password = password;
        this.orgName = orgName;
        this.licenseNumber = licenseNumber;
        this.scope = scope;
        this.enabled = enabled;
    }
}
