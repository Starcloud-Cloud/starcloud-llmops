package com.starcloud.ops.business.app.enums.plan;

import com.starcloud.ops.business.app.enums.market.AppMarketTagTypeEnum;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Getter
public enum CreativeTypeEnum implements IEnumable<Integer> {

    /**
     * 小红书创作计划
     */
    XHS(1, "小红书创作计划", AppMarketTagTypeEnum.XIAO_HONG_SHU_WRITING);

    /**
     * 采样器 code
     */
    private final Integer code;

    /**
     * 采样器 label
     */
    private final String label;

    /**
     * 标签类型
     */
    private final AppMarketTagTypeEnum tagType;

    CreativeTypeEnum(Integer code, String label, AppMarketTagTypeEnum tagType) {
        this.code = code;
        this.label = label;
        this.tagType = tagType;
    }

    /**
     * 根据名称获取枚举
     *
     * @param name 根据枚举获取类型
     * @return 枚举
     */
    public static CreativeTypeEnum of(String name) {
        for (CreativeTypeEnum item : values()) {
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
