package com.spring.nuqta.donation.Entity;

import com.spring.nuqta.base.Entity.BaseEntity;
import com.spring.nuqta.enums.DonStatus;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "donation")
public class DonEntity extends BaseEntity<Long> {

    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "donation_date")
    private LocalDate donationDate;

    @Column(name = "last_donation")
    private LocalDate lastDonation;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "payment_offered")
    private Boolean paymentOffered;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DonStatus status;

    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(name = "location", columnDefinition = "GEOGRAPHY")
    private Geometry location;

    @Column(name = "weight")
    private Long weight;


    @OneToOne(mappedBy = "donation")
    private UserEntity user;

//    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "request_id", referencedColumnName = "id")
//    private ReqEntity request;

}
