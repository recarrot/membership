package com.membership.entity;

import lombok.Data;
import javax.persistence.Id;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String idNumber;

    private Integer age;

    @Column(nullable = false)
    private LocalDateTime joinDate;

    @Column(nullable = false)
    private Integer consumptionCount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmountBeforeDeduction;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private Boolean isActive;

    private String phoneNumber;
}