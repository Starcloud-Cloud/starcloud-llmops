package com.starcloud.ops.business.limits.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户权益 - 策略类型的枚举
 * 枚举值
 *
 * @author AlanCusack
 */
@Getter
@AllArgsConstructor
public enum BenefitsDiscountTypeEnums {

    /**
     * 直接优惠
     */
    DIRECT_DISCOUNT("直接优惠"),

    /**
     *  按比例优惠
     */
    PERCENTAGE_DISCOUNT( "按比例优惠"),

    ;
    /**
     * name
     */
    private final String name;

}
