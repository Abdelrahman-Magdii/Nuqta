package com.spring.nuqta.OtpMail.Entity;

import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Otp_Verify")
public class OtpVerifyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String otp;

    @Basic(optional = false)
    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "org_id", referencedColumnName = "id")
    private OrgEntity organization;


    public boolean isExpired() {
        return getExpiredAt().isBefore(LocalDateTime.now());
    }
}
