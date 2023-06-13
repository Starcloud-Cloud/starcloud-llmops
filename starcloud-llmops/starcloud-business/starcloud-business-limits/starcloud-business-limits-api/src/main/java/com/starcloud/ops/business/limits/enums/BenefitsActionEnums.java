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
public enum BenefitsActionEnums {

    /**
     * 新增
     */
    ADD("ADD", "新增"),

    /**
     * 过期
     */
    EXPIRE("EXPIRE", "过期"),

    /**
     * 使用
     */
    USED("USED", "使用"),

    ;

    /**
     * code
     */
    private final String code;

    /**
     * 中文名称
     */
    private final String chineseName;


    public static BenefitsActionEnums getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }
}
