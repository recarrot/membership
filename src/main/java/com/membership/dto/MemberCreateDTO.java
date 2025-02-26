package com.membership.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class MemberCreateDTO {
    @NotBlank(message = "会员卡号不能为空")
    private String cardNumber;

    @NotBlank(message = "会员名称不能为空")
    private String name;

//    @NotBlank(message = "身份证号不能为空")
//    @Pattern(regexp = "^\\d{17}[0-9X]$", message = "身份证号格式不正确")
    private String idNumber;

//    @NotBlank(message = "手机号不能为空")
    private String phoneNumber;

    private Integer age;
}

