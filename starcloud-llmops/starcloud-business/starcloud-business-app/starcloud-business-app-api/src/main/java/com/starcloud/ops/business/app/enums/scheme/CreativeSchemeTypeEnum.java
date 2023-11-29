package com.starcloud.ops.business.app.enums.scheme;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Getter
public enum CreativeSchemeTypeEnum implements IEnumable<Integer> {

    SYSTEM(1, "系统类型"),


    USER(2, "用户类型");

    /**
     * 类型编码
     */
    private final Integer code;

    /**
     * 类型名称
     */
    private final String label;

    /**
     * 创作计划类型枚举
     *
     * @param code  类型编码
     * @param label 类型名称
     */
    CreativeSchemeTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
