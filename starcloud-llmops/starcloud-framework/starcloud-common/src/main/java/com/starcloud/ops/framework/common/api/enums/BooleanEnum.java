package com.starcloud.ops.framework.common.api.enums;

import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Getter
public enum BooleanEnum implements IEnumable<Integer> {

    /**
     * TRUE
     */
    TRUE(1, Boolean.TRUE, "TRUE"),

    /**
     * 禁用
     */
    FALSE(0, Boolean.FALSE, "FALSE");

    /**
     * 状态code
     */
    private final Integer code;

    /**
     * 布尔值
     */
    private final Boolean bool;

    /**
     * 状态描述
     */
    private final String label;

    BooleanEnum(Integer code, Boolean bool, String label) {
        this.code = code;
        this.bool = bool;
        this.label = label;
    }


}
