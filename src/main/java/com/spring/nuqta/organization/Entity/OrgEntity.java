package com.spring.nuqta.organization.Entity;

import com.spring.nuqta.base.Entity.BaseEntity;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.request.Entity.ReqEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Geometry;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "organization")
public class OrgEntity extends BaseEntity<Long> {

    @NotBlank(message = "Organization name cannot be blank")
    @Size(max = 100, message = "Organization name cannot exceed 100 characters")
    private String orgName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotNull(message = "Location cannot be null")
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(name = "location", columnDefinition = "GEOGRAPHY")
    private Geometry location;

    @NotBlank(message = "Phone number cannot be blank")
    @Size(max = 15, message = "Phone number cannot exceed 15 characters")
    private String phoneNumber;

    @NotBlank(message = "License number cannot be blank")
    @Size(max = 50, message = "License number cannot exceed 50 characters")
    private String licenseNumber;

    @NotNull(message = "Scope cannot be null")
    @Enumerated(EnumType.STRING)
    private Scope scope;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReqEntity> requests;

    public OrgEntity(String org_name, String email, String password, Geometry location, String phoneNumber, String licenseNumber, Scope scope) {
        this.orgName = org_name;
        this.email = email;
        this.password = password;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
        this.scope = scope;
    }
}