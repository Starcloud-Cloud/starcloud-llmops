package com.starcloud.ops.business.app.enums.xhs.scheme;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Getter
public enum CreativeSchemeGenerateModeEnum implements IEnumable<Integer> {

    /**
     * 随机图文生成
     */
    RANDOM(1, "随机获取"),

    /**
     * 干货文生成
     */
    AI_PARODY(2, "AI仿写"),

    /**
     * 自定义
     */
    AI_CUSTOM(3, "AI自定义");

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
    CreativeSchemeGenerateModeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
