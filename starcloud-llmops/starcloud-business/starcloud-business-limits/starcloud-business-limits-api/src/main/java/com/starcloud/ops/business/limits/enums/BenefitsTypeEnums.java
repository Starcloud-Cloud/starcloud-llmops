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
public enum BenefitsTypeEnums {

    /**
     * 应用
     */
    APP("APP", "应用","app"),

    /**
     * 数据集
     */
    DATASET("DATASET", "数据集","dataset"),

    /**
     * 图片
     */
    IMAGE("IMAGE", "图片","image"),

    /**
     * 令牌
     */
    TOKEN("TOKEN", "令牌","token"),

    ;

    /**
     * code
     */
    private final String code;

    /**
     * 中文名称
     */
    private final String chineseName;

    /**
     * 数据库字段前缀
     */
    private final String dataPrefix;


    public static BenefitsTypeEnums getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }
}
