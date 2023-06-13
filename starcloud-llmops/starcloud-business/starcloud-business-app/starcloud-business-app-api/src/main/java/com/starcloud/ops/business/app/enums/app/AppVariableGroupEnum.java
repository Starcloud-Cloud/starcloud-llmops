package com.starcloud.ops.business.app.enums.app;

import lombok.Getter;

/**
 * 变量分组
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppVariableGroupEnum {

    /**
     * 变量类型为
     */
    SYSTEM(0, "系统"),

    /**
     * 参数变量
     */
    PARAMS(1, "参数"),

    /**
     * 模型变量
     */
    MODEL(2, "模型");

    /**
     * 变量组Code
     */
    @Getter
    private final Integer code;

    /**
     * 变量组说明
     */
    @Getter
    private final String message;

    /**
     * 构造函数
     *
     * @param code    变量组 Code
     * @param message 变量组说明
     */
    AppVariableGroupEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
