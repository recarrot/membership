package com.membership.entity;

import com.membership.enums.ConsumptionType;
import com.membership.enums.TransactionType;
import lombok.Data;
import javax.persistence.Id;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    //会员卡号
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    //类型：充值/消费
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    //消费类型：长线/短线
    private ConsumptionType consumptionType;

    @Column(nullable = false, precision = 10, scale = 2)
    //金额
    private BigDecimal amount;

    @Column(nullable = false)
    //出行目的地
    private String destination;

    //备注
    private String remark;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}