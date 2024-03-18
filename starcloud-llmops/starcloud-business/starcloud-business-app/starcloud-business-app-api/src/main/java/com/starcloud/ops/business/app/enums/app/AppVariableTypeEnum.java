package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 变量类型
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Getter
public enum AppVariableTypeEnum implements IEnumable<Integer> {

    /**
     * 文本变量类型
     */
    TEXT(0, "文本"),

    /**
     * 图片变量类型
     */
    IMAGE(1, "图片");

    /**
     * 变量类型Code
     */
    private final Integer code;

    /**
     * 变量类型说明
     */
    private final String label;

    /**
     * 构造函数
     *
     * @param code  变量类型 Code
     * @param label 变量类型说明
     */
    AppVariableTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
