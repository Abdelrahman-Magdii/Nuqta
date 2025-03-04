package com.spring.nuqta.request.Entity;

import com.spring.nuqta.base.Entity.BaseEntity;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.enums.Level;
import com.spring.nuqta.enums.Status;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "requests")
@DynamicUpdate
public class ReqEntity extends BaseEntity<Long> {

    @NotNull(message = "Blood type is required.")
    @Column(name = "blood_type_needed", nullable = false)
    private String bloodTypeNeeded;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "request_date", nullable = false, updatable = false)
    private LocalDate requestDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", nullable = false)
    private Level urgencyLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "payment_available")
    private Boolean paymentAvailable;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "conservatism", nullable = false)
    private String conservatism;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", referencedColumnName = "id")
    private OrgEntity organization;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "request_donation",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "donation_id")
    )
    private Set<DonEntity> donations = new HashSet<>();

}
