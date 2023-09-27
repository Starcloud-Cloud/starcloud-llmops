package com.starcloud.ops.business.log.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 */
@Getter
public enum LogStatusEnum implements IEnumable<Integer> {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),

    /**
     * 失败
     */
    ERROR(1, "失败");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String label;

    /**
     * 构造方法
     *
     * @param code  状态码
     * @param label 状态描述
     */
    LogStatusEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
