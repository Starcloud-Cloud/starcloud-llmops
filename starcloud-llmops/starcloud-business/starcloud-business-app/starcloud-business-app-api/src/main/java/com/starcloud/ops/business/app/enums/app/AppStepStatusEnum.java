package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Getter
public enum AppStepStatusEnum implements IEnumable<String> {

    /**
     * 待执行
     */
    INIT(CreativeContentStatusEnum.INIT.name(), "待执行"),

    /**
     * 执行中
     */
    EXECUTING(CreativeContentStatusEnum.EXECUTING.name(), "执行中"),

    /**
     * 执行成功
     */
    SUCCESS(CreativeContentStatusEnum.SUCCESS.name(), "执行成功"),

    /**
     * 执行失败
     */
    FAILURE(CreativeContentStatusEnum.FAILURE.name(), "执行失败"),

    ;

    /**
     * 步骤状态Code
     */
    private final String code;

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
    AppStepStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

}