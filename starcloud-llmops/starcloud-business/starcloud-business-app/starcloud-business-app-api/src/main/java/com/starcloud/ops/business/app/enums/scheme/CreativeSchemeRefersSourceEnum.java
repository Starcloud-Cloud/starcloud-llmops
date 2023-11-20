package com.starcloud.ops.business.app.enums.scheme;

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
public enum CreativeSchemeRefersSourceEnum implements IEnumable<Integer> {

    SMALL_RED_BOOK(1, "小红书"),


    OTHER(2, "其他");

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
    CreativeSchemeRefersSourceEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 获取类型枚举
     *
     * @return 类型枚举
     */
    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(CreativeSchemeRefersSourceEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getLabel());
                    option.setValue(item.name());
                    return option;
                }).collect(Collectors.toList());
    }
}
