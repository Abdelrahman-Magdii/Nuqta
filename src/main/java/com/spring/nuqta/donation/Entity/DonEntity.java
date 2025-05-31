package com.spring.nuqta.donation.Entity;

import com.spring.nuqta.base.Entity.BaseEntity;
import com.spring.nuqta.enums.DonStatus;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "donation")
@DynamicUpdate
public class DonEntity extends BaseEntity<Long> {

    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "donation_date")
    private LocalDateTime donationDate = LocalDateTime.now();

    @Column(name = "last_quiz_date")
    private LocalDate lastQuizDate;

    @Column(name = "last_donation")
    private LocalDate lastDonation = LocalDate.now();

    @Column(name = "amount")
    private Double amount;

    @Column(name = "payment_offered")
    private Boolean paymentOffered;

    @Column(name = "confirm_Donate")
    private Boolean confirmDonate = false;

    @Column(name = "confirm_Donate_Req_Id")
    private Long confirmDonateReqId = 0L;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DonStatus status;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "city")
    private String city;

    @Column(name = "conservatism")
    private String conservatism;

    @OneToOne(mappedBy = "donation", cascade = CascadeType.ALL)
    private UserEntity user;

    @ManyToMany(mappedBy = "donations", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    private Set<ReqEntity> acceptedRequests = new HashSet<>();

//    public boolean isExpired() {
//        LocalDate currentDate = LocalDate.now();
//        LocalDate expiryDate = this.getDonationDate().plusMonths(3);
//        return currentDate.isAfter(expiryDate) || currentDate.isEqual(expiryDate);
//    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDateTime = this.getDonationDate().plusSeconds(1);
        return now.isAfter(expiryDateTime) || now.isEqual(expiryDateTime);
    }

    public void addAcceptedRequest(ReqEntity request) {
        this.acceptedRequests.add(request);
        request.getDonations().add(this);
    }
}
