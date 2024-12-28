package com.spring.nuqta.usermanagement.Entity;

import com.spring.nuqta.base.Entity.BaseEntity;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.request.Entity.ReqEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Users")
public class UserEntity extends BaseEntity<Long> {

    private String username;

    private String email;

    private String password;

    private Integer age;

    private String phone_number;

    @Enumerated(EnumType.STRING)
    private Scope scope;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "donation_id", referencedColumnName = "id")
    private DonEntity donation;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReqEntity> request;


}
