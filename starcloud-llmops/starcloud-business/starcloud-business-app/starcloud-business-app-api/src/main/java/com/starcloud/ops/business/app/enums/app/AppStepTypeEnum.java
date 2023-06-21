package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 步骤类型枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppStepTypeEnum implements IEnumable<Integer> {

    /**
     * 通用型步骤
     */
    WORKFLOW(0, "工作流型步骤"),

    /**
     * 适配型步骤
     */
    FUNCTION(1, "函数型步骤");

    /**
     * 步骤类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 步骤类型说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  步骤类型 Code
     * @param label 步骤类型说明
     */
    AppStepTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
