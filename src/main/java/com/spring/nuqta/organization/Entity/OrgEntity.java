package com.spring.nuqta.organization.Entity;

import com.spring.nuqta.base.Entity.BaseEntity;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.forgotPassword.Entity.ResetPasswordEntity;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.verificationToken.Entity.VerificationToken;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "organization")
@DynamicUpdate
public class OrgEntity extends BaseEntity<Long> {

    @NotBlank(message = "Organization name cannot be blank")
    @Size(max = 100, message = "Organization name cannot exceed 100 characters")
    @Column(unique = true, nullable = false, length = 50)
    private String orgName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City name cannot exceed 100 characters")
    private String city;

    @NotBlank(message = "Conservatism level cannot be blank")
    @Size(max = 50, message = "Conservatism level cannot exceed 50 characters")
    private String conservatism;

    @NotBlank(message = "Phone number cannot be blank")
    @Size(max = 15, message = "Phone number cannot exceed 15 characters")
    private String phoneNumber;

    @NotBlank(message = "License number cannot be blank")
    @Size(max = 50, message = "License number cannot exceed 50 characters")
    @Column(unique = true, nullable = false, length = 100)
    private String licenseNumber;

    @NotNull(message = "Scope cannot be null")
    @Enumerated(EnumType.STRING)
    private Scope scope;

    private boolean enabled = false;

    @Column(name = "fcm_token")
    private String fcmToken;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ReqEntity> uploadedRequests;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private Set<VerificationToken> verificationTokens;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
    private Set<ResetPasswordEntity> resetPasswordEntities;

}