package com.starcloud.ops.business.app.enums.app;

import lombok.Getter;

/**
 * 步骤类型枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppStepTypeEnum {

    /**
     * 通用型步骤
     */
    COMMON(0, "通用型步骤"),

    /**
     * 适配型步骤
     */
    ADAPTER(1, "适配型步骤");

    /**
     * 步骤类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 步骤类型说明
     */
    @Getter
    private final String message;

    /**
     * 构造函数
     *
     * @param code    步骤类型 Code
     * @param message 步骤类型说明
     */
    AppStepTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
