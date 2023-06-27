package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 变量分组
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppVariableGroupEnum implements IEnumable<Integer> {

    /**
     * 变量类型为
     */
    SYSTEM(0, "应用内全局参数"),

    /**
     * 参数变量
     */
    PARAMS(1, "参数"),

    /**
     * 模型变量
     */
    MODEL(2, "具体步骤模型参数");

    /**
     * 变量组Code
     */
    @Getter
    private final Integer code;

    /**
     * 变量组说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  变量组 Code
     * @param label 变量组说明
     */
    AppVariableGroupEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
