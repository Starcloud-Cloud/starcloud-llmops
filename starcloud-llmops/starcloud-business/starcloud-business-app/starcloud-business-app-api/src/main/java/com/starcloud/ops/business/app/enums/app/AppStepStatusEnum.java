package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Getter
public enum AppStepStatusEnum implements IEnumable<Integer> {

    /**
     * 待执行
     */
    WAITING(0, "待执行"),

    /**
     * 执行中
     */
    RUNNING(1, "执行中"),

    /**
     * 执行成功
     */
    SUCCESS(2, "执行成功"),

    /**
     * 执行失败
     */
    FAILED(3, "执行失败"),

    ;

    /**
     * 步骤状态Code
     */
    private final Integer code;

    /**
     * 步骤状态说明
     */
    private final String label;

    /**
     * 构造函数
     *
     * @param code  步骤状态Code
     * @param label 步骤状态说明
     */
    AppStepStatusEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}