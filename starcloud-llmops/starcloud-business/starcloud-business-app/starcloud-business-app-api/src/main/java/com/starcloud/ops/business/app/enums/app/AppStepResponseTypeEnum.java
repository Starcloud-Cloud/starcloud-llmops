package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 步骤返回返回类型枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppStepResponseTypeEnum implements IEnumable<Integer> {

    /**
     * 文本类型
     */
    TEXT(0, "文本类型"),

    /**
     * 数组类型
     */
    ARRAY(1, "数组类型"),

    /**
     * 跳转类型
     */
    REDIRECT(2, "跳转类型"),

    /**
     * 复制类型
     */
    COPY(3, "复制类型"),

    /**
     * 弹窗类型
     */
    DIALOG(4, "弹窗类型");

    /**
     * 步骤返回类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 步骤返回类型说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  步骤返回类型 Code
     * @param label 步骤返回类型说明
     */
    AppStepResponseTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
