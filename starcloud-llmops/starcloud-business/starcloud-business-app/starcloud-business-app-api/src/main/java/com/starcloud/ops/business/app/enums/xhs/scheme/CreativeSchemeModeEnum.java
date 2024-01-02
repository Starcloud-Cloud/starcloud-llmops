package com.starcloud.ops.business.app.enums.xhs.scheme;

import com.starcloud.ops.business.app.enums.market.AppMarketTagTypeEnum;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Getter
public enum CreativeSchemeModeEnum implements IEnumable<Integer> {

    /**
     * 随机图文生成
     */
    RANDOM_IMAGE_TEXT(1, "随机图文生成", AppMarketTagTypeEnum.XIAO_HONG_SHU_WRITING),

    /**
     * 干货文生成
     */
    PRACTICAL_IMAGE_TEXT(2, "干货文生成", AppMarketTagTypeEnum.XIAO_HONG_SHU_PRACTICAL_WRITING),

    /**
     * 自定义
     */
    CUSTOM_IMAGE_TEXT(3, "自定义", AppMarketTagTypeEnum.XIAO_HONG_SHU_WRITING);

    /**
     * 类型编码
     */
    private final Integer code;

    /**
     * 类型名称
     */
    private final String label;

    /**
     * 标签类型
     */
    private final AppMarketTagTypeEnum tagType;

    /**
     * 创作计划类型枚举
     *
     * @param code    类型编码
     * @param label   类型名称
     * @param tagType 标签类型
     */
    CreativeSchemeModeEnum(Integer code, String label, AppMarketTagTypeEnum tagType) {
        this.code = code;
        this.label = label;
        this.tagType = tagType;
    }
}
