package com.starcloud.ops.business.limits.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 套餐枚举
 *
 * @author AlanCusack
 */
@Getter
@AllArgsConstructor
public enum ProductTimeEnum {

    WEEK("week", "周付"),
    YEAR("year", " 月付"),
    MONTH("month", "年付"),
    ;

    /**
     * 类型
     */
    private final String type;

    /**
     * 名称
     */
    private final String name;


    public static ProductTimeEnum getByCode(String type) {
        return ArrayUtil.firstMatch(o -> o.getType().equals(type), values());
    }

}
