package com.starcloud.ops.business.app.enums.app;

import lombok.Getter;

/**
 * 步骤返回样式枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppStepResponseStyleEnum {

    /**
     * 输入框样式
     */
    INPUT(0, "输入框样式"),

    /**
     * 文本框样式
     */
    TEXTAREA(1, "文本框样式"),

    /**
     * 图片样式
     */
    IMAGE(2, "图片样式"),

    /**
     * Base64图片样式
     */
    BASE64(3, "Base64样式"),

    /**
     * 按钮样式
     */
    BUTTON(4, "按钮样式"),

    /**
     * 商品样式
     */
    PRODUCT(5, "商品样式");

    /**
     * 步骤返回样式Code
     */
    @Getter
    private final Integer code;

    /**
     * 步骤返回样式说明
     */
    @Getter
    private final String message;

    /**
     * 构造函数
     *
     * @param code    步骤返回样式 Code
     * @param message 步骤返回样式说明
     */
    AppStepResponseStyleEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
