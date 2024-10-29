package com.starcloud.ops.business.app.enums.xhs.plan;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Getter
public enum CreativePlanStatusEnum implements IEnumable<Integer> {

    /**
     * 待执行
     */
    PENDING(1, "待执行"),

    /**
     * 执行中
     */
    RUNNING(2, "执行中"),

    /**
     * 已暂停
     */
    PAUSE(3, "暂停"),

    /**
     * 已取消
     */
    CANCELED(4, "已取消"),

    /**
     * 已完成
     */
    COMPLETE(5, "已完成"),

    /**
     * 失败
     */
    FAILURE(6, "失败");

    /**
     * 采样器 code
     */
    private final Integer code;

    /**
     * 采样器 label
     */
    private final String label;

    CreativePlanStatusEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    public static Boolean contains(String name) {
        return Arrays.stream(CreativePlanStatusEnum.values()).anyMatch(e -> e.name().equalsIgnoreCase(name));
    }

    /**
     * 判断是否可以修改状态
     *
     * @param name 状态
     * @return 是否可以修改状态
     */
    public static Boolean canModifyStatus(String name) {
        return CreativePlanStatusEnum.PENDING.name().equals(name)
                || CreativePlanStatusEnum.COMPLETE.name().equals(name)
                || CreativePlanStatusEnum.FAILURE.name().equals(name)
                || CreativePlanStatusEnum.CANCELED.name().equals(name);
    }
}
