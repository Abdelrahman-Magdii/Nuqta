package com.spring.nuqta.usermanagement.Entity;

import com.spring.nuqta.base.Entity.BaseEntity;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.enums.Gender;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.request.Entity.ReqEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Users")
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "donation_id", referencedColumnName = "id")
    private DonEntity donation;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReqEntity> requests;

    public UserEntity(String username, String email, String password, LocalDate birthDate, String phoneNumber, Scope scope, DonEntity donation, Gender gender) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.scope = scope;
        this.donation = donation;
        this.gender = gender;
    }


}
