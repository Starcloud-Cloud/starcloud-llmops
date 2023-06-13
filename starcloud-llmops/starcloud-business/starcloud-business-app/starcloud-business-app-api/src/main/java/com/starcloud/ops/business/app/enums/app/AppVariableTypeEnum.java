package com.starcloud.ops.business.app.enums.app;

import lombok.Getter;

/**
 * 变量类型
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppVariableTypeEnum {

    /**
     * 文本变量类型
     */
    TEXT(0, "文本");

    /**
     * 变量类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 变量类型说明
     */
    @Getter
    private final String message;

    /**
     * 构造函数
     *
     * @param code    变量类型 Code
     * @param message 变量类型说明
     */
    AppVariableTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
