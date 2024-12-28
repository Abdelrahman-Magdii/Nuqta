package com.spring.nuqta.request.Entity;

import com.spring.nuqta.base.Entity.BaseEntity;
import com.spring.nuqta.enums.Level;
import com.spring.nuqta.enums.Status;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "requests")
public class ReqEntity extends BaseEntity<Long> {

    @NotNull(message = "Blood type is required.")
    @Column(name = "blood_type_needed", nullable = false)
    private String bloodTypeNeeded;

    @Column(name = "amount")
    private Double amount;

    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(name = "location", columnDefinition = "GEOGRAPHY")
    private Geometry location;

    @Column(name = "address")
    private String address;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level")
    private Level urgencyLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "payment_available")
    private Boolean paymentAvailable;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "org_id", referencedColumnName = "id")
    private OrgEntity organization;

//    @OneToMany(mappedBy = "request", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    private Set<DonEntity> donation;

}
