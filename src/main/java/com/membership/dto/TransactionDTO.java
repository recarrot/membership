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

//    @NotNull(message = "必须选择出行线路类型")
    private ConsumptionType consumptionType;

    @NotNull(message = "必须填写出行目的地")
    private String destination;

    private String remark;
}