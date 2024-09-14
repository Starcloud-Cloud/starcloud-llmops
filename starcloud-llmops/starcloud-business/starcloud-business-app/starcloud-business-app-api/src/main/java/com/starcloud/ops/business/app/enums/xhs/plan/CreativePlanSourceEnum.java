package com.starcloud.ops.business.app.enums.xhs.plan;

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
 * @since 2023-11-07
 */
@Getter
public enum CreativePlanSourceEnum implements IEnumable<Integer> {

    /**
     * 小红书创作计划
     */
    APP(1, "我的应用"),

    /**
     * 应用市场创作计划
     */
    MARKET(2, "应用市场");

    /**
     * 采样器 code
     */
    private final Integer code;

    /**
     * 采样器 label
     */
    private final String label;


    CreativePlanSourceEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 判断是否是我的应用
     *
     * @param source 创作计划来源
     * @return 是否是我的应用
     */
    public static Boolean isApp(String source) {
        return APP.name().equalsIgnoreCase(source);
    }

    /**
     * 判断是否是应用市场
     *
     * @param source 创作计划来源
     * @return 是否是我的应用
     */
    public static Boolean isMarket(String source) {
        return MARKET.name().equalsIgnoreCase(source);
    }

    /**
     * 根据名称获取枚举
     *
     * @param name 根据枚举获取类型
     * @return 枚举
     */
    public static CreativePlanSourceEnum of(String name) {
        for (CreativePlanSourceEnum item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 获取Option列表
     *
     * @return Option列表
     */
    public static List<Option> options() {
        return Arrays.stream(values())
                .sorted(Comparator.comparingInt(CreativePlanSourceEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setValue(item.name());
                    option.setLabel(item.getLabel());
                    return option;
                })
                .collect(Collectors.toList());
    }
}
