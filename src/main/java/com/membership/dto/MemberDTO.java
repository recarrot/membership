package com.membership.dto;

import com.membership.enums.ConsumptionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// src/main/java/com/membership/dto/MemberDTO.java
@Data
public class MemberDTO {
    private Long id;
    private String cardNumber;
    private String name;
    private String phoneNumber;
    private String idNumber;
    private Integer age;
    private LocalDateTime joinDate;
    private Integer consumptionCount;
    private BigDecimal totalAmount;
    private BigDecimal balance;
    private Boolean isActive;
    private ConsumptionType consumptionType;
}

// src/main/java/com/membership/dto/MemberStatisticsDTO.java


// src/main/java/com/membership/dto/LoginRequest.java


