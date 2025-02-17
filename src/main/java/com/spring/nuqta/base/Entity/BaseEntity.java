package com.spring.nuqta.base.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity<ID extends Number> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private ID id;

    @Column(updatable = false)
    @CreationTimestamp
    @CreatedDate
    private LocalDate createdDate;

    @Column(updatable = false)
    @CreatedBy
    private String createdUser;

    @LastModifiedDate
    @UpdateTimestamp
    private LocalDate modifiedDate;

    @LastModifiedBy
    private String modifiedUser;

}
