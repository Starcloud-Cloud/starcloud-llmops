package com.starcloud.ops.framework.common.api.enums;

import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Getter
public enum StateEnum implements IEnumable<Integer> {

    /**
     * 启用
     */
    ENABLE(0, "启用"),

    /**
     * 禁用
     */
    DISABLE(1, "禁用");

    /**
     * 状态code
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String label;

    StateEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }


}
