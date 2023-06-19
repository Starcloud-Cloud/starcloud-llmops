package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 变量样式枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppVariableStyleEnum implements IEnumable<Integer> {

    /**
     * 输入框样式
     */
    INPUT(0, "输入框样式"),

    /**
     * 文本框样式
     */
    TEXTAREA(1, "文本框样式"),

    /**
     * 下拉框样式
     */
    SELECT(2, "下拉框样式");

    /**
     * 变量样式Code
     */
    @Getter
    private final Integer code;

    /**
     * 变量样式说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  变量样式 Code
     * @param label 变量样式说明
     */
    AppVariableStyleEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
