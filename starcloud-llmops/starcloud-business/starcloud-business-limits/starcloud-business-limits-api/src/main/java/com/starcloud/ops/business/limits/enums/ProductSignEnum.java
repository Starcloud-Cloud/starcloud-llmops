package com.starcloud.ops.business.limits.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 套餐枚举
 *
 * @author AlanCusack
 */
@Getter
@AllArgsConstructor
public enum ProductSignEnum {

    PRODUCT_BASIC_CONFIG("魔法ai-基础版签约配置", 1, "MONTH", 1, 1, null, null),
    ;

    /**
     * 配置名称
     */
    private final String name;
    /**
     * 首次支付价格 分
     */
    private final Integer firstAmount;

    /**
     * 周期类型 ，枚举值为 DAY 和 MONTH。
     */
    private final String periodType;

    /**
     * 周期数
     */
    private final Integer period;

    /**
     * 单次扣款最大金额，必填，即每次发起扣款时限制的最大金额，单位为元。商家每次发起扣款都不允许大于此金额。
     */
    private final Integer singleAmount;

    /**
     * 订单总金额。首次支付金额，不算在周期扣总金额里面。。
     */
    private final Integer totalAmount;

    /**
     * 总扣款次数。
     */
    private final Integer totalPayments;



}
