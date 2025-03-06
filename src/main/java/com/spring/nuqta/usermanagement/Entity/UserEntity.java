package com.spring.nuqta.usermanagement.Entity;

import com.spring.nuqta.base.Entity.BaseEntity;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.enums.Gender;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.forgotPassword.Entity.ResetPasswordEntity;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.verificationToken.Entity.VerificationToken;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Users")
@DynamicUpdate
public class UserEntity extends BaseEntity<Long> {

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Scope scope;

    private boolean enabled = false;

    @Column(name = "fcm_token", nullable = false)
    private String fcmToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ReqEntity> uploadedRequests;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "donation_id", referencedColumnName = "id")
    private DonEntity donation;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<VerificationToken> verificationTokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<ResetPasswordEntity> resetPasswordEntities;

}
