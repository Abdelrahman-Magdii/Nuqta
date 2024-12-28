package com.spring.nuqta.organization.Entity;

import com.spring.nuqta.base.Entity.BaseEntity;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.request.Entity.ReqEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    private String org_name;

    private String email;

    private String password;
    
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(name = "location", columnDefinition = "GEOGRAPHY")
    private Geometry location;

    private String phone_number;

    private String license_number;

    @Enumerated(EnumType.STRING)
    private Scope scope;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReqEntity> requests;
}
