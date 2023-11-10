package com.starcloud.ops.business.limits.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.math3.stat.descriptive.summary.Product;

/**
 * 用户权益 - 策略类型的枚举
 * 枚举值
 *
 * @author AlanCusack
 */
@Getter
@AllArgsConstructor
public enum BenefitsTypesEnums {

    /**
     * 基础权益
     */
    BASIC_BENEFITS(0,"基础权益"),

    /**
     * 支付权益
     */
    PAY_BENEFITS(1,"支付权益")
    ;

    private final Integer code;

    private final String name;
}
