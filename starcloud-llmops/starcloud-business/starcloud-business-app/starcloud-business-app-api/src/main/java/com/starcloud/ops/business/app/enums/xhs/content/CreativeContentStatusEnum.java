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
public enum CreativeContentStatusEnum implements IEnumable<Integer> {

    /**
     * 初始化
     */
    INIT(0, "初始化"),

    /**
     * 执行中
     */
    EXECUTING(1, "执行中"),

    /**
     * 执行成功
     */
    SUCCESS(2, "执行成功"),

    /**
     * 执行失败
     */
    FAILURE(3, "执行失败"),

    /**
     * 最终失败，且失败次数大于阈值时候
     */
    ULTIMATE_FAILURE(4, "执行最终失败"),

    /**
     * 已取消
     */
    CANCELED(5, "已取消");

    /**
     * 状态编码
     */
    private final Integer code;

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
    CreativeContentStatusEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
