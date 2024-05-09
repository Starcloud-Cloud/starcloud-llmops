package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 变量类型
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Getter
public enum AppVariableTypeEnum implements IEnumable<Integer> {

    /**
     * 文本变量类型
     */
    TEXT(0, "文本"),

    /**
     * 图片变量类型
     */
    IMAGE(1, "图片");

    /**
     * 变量类型Code
     */
    private final Integer code;

    /**
     * 变量类型说明
     */
    private final String label;

    /**
     * 构造函数
     *
     * @param code  变量类型 Code
     * @param label 变量类型说明
     */
    AppVariableTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    public static List<Option> options() {
        return Arrays.stream(values())
                .filter(item -> !item.equals(IMAGE))
                .map(AppVariableTypeEnum::option)
                .collect(Collectors.toList());
    }

    public static Option option(AppVariableTypeEnum typeEnum) {
        Option option = new Option();
        option.setLabel(typeEnum.getLabel());
        option.setValue(typeEnum.name());
        return option;
    }
}
