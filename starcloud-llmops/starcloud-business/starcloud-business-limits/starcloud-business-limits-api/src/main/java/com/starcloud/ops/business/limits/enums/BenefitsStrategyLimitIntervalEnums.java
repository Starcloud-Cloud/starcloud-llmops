package com.starcloud.ops.business.limits.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户权益 - 策略类型的枚举
 * 枚举值
 *
 * @author AlanCusack
 */
@Getter
@AllArgsConstructor
public enum BenefitsStrategyLimitIntervalEnums {
    /**
     * 仅一次
     */
    ONCE_ONLY("ONCE_ONLY", " 仅一次", "Once Only"),

    /**
     * 天
     */
    DAY("DAY", "天", "DAY"),

    /**
     * 周
     */
    WEEK("WEEK", "周", "WEEK"),

    /**
     * 月
     */
    MONTH("MONTH", "月", "MONTH"),

    /**
     * 年
     */
    YEAR("YEAR", "年", "YEAR"),

    ;

    /**
     * code
     */
    private final String name;

    /**
     * 中文名称
     */
    private final String chineseName;

    /**
     * 英文名称
     */
    private final String englishName;


    public static BenefitsStrategyLimitIntervalEnums getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getName().equals(code), values());
    }
}
