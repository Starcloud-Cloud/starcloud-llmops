package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 步骤来源枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppStepSourceEnum implements IEnumable<Integer> {

    /**
     * 原生步骤
     */
    NATIVE(0, "原生步骤"),

    /**
     * 扩展步骤
     */
    EXTEND(1, "扩展步骤");

    /**
     * 步骤来源Code
     */
    @Getter
    private final Integer code;

    /**
     * 步骤来源型说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  步骤来源 Code
     * @param label 步骤来源说明
     */
    AppStepSourceEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
