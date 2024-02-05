package com.starcloud.ops.business.app.enums.xhs.scheme;

import com.starcloud.ops.business.app.enums.market.AppMarketTagTypeEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    CUSTOM_IMAGE_TEXT(3, "自定义", AppMarketTagTypeEnum.XIAO_HONG_SHU_CUSTOM_WRITING);

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

    /**
     * 获取类型枚举
     *
     * @return 类型枚举
     */
    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(CreativeSchemeModeEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getLabel());
                    option.setValue(item.name());
                    return option;
                }).collect(Collectors.toList());
    }
}
