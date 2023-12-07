package com.starcloud.ops.business.app.enums.xhs.content;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 状态枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2020-11-03 10:54
 */
@Getter
public enum CreativeContentStatusEnum implements IEnumable<String> {

    /**
     * 初始化
     */
    INIT("init", "初始化"),

    /**
     * 执行中
     */
    EXECUTING("executing", "执行中"),

    /**
     * 执行成功
     */
    EXECUTE_SUCCESS("execute_success", "执行成功"),

    /**
     * 执行失败
     */
    EXECUTE_ERROR("execute_error", "执行失败"),

    /**
     * 执行失败，且失败次数大于阈值时候
     */
    EXECUTE_ERROR_FINISHED("execute_error_finished", "执行最终错误");

    /**
     * 状态编码
     */
    private final String code;

    /**
     * 状态名称
     */
    private final String label;

    /**
     * 创作计划类型枚举
     *
     * @param code  类型编码
     * @param label 状态名称
     */
    CreativeContentStatusEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
