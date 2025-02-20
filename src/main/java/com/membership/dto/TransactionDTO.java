package com.membership.dto;

import com.membership.enums.ConsumptionType;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransactionDTO {
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    // 移除此字段的@NotNull验证，改为可选字段
    private BigDecimal deductionAmount;

    private ConsumptionType consumptionType;

    // 目的地仅当消费类型为CONSUMPTION时必填
    private String destination;

    private String remark;
}