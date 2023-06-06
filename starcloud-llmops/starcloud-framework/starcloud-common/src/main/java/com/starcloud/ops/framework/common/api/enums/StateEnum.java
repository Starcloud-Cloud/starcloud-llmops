package com.starcloud.ops.framework.common.api.enums;

import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public enum StateEnum {

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
    @Getter
    private final Integer code;

    /**
     * 状态描述
     */
    @Getter
    private final String message;

    StateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


}
