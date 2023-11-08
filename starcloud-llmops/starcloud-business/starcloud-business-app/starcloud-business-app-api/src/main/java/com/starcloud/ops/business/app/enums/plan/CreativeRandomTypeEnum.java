package com.starcloud.ops.business.app.enums.plan;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Getter
public enum CreativeRandomTypeEnum implements IEnumable<Integer> {

    /**
     * 全部随机
     */
    RANDOM(1, "全部随机"),

    /**
     * 按顺序
     */
    SEQUENCE(2, "按顺序");

    /**
     * 采样器 code
     */
    private final Integer code;

    /**
     * 采样器 label
     */
    private final String label;

    CreativeRandomTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 根据名称获取枚举
     *
     * @param name 根据枚举获取类型
     * @return 枚举
     */
    public static CreativeRandomTypeEnum of(String name) {
        for (CreativeRandomTypeEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 判断是否包含
     *
     * @param name 名称
     * @return 是否包含
     */
    public static Boolean contains(String name) {
        return Arrays.stream(values()).anyMatch(item -> item.name().equals(name));
    }
}
