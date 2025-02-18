package com.membership.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberStatisticsDTO {
    private Long totalMembers;
    private Long activeMembers;
    private BigDecimal totalConsumption;
    private BigDecimal averageConsumption;
}