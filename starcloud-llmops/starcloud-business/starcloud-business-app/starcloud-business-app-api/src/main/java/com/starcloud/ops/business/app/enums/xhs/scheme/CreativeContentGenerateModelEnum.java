package com.starcloud.ops.business.app.enums.xhs.scheme;

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
public enum CreativeContentGenerateModelEnum implements IEnumable<Integer> {

    /**
     * 自定义
     */
    AI_CUSTOM(1, "AI生成"),

    /**
     * AI仿写
     */
    AI_PARODY(2, "AI仿写"),

    /**
     * 随机图文生成
     */
    RANDOM(3, "随机获取");

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
    CreativeContentGenerateModelEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 获取类型枚举
     *
     * @return 类型枚举
     */
    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(CreativeContentGenerateModelEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getLabel());
                    option.setValue(item.name());
                    return option;
                }).collect(Collectors.toList());
    }
}
