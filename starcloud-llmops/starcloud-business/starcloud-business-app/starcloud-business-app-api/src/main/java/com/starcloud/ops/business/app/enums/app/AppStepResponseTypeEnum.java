package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 步骤返回返回类型枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Getter
public enum AppStepResponseTypeEnum implements IEnumable<Integer> {

    /**
     * 文本类型
     */
    TEXT(0, "文本类型"),

    /**
     * JSON类型
     */
    JSON(2, "JSON类型"),

    /**
     * 图片样式
     */
    IMAGE(3, "图片类型");

    /**
     * 步骤返回类型Code
     */
    private final Integer code;

    /**
     * 步骤返回类型说明
     */
    private final String label;

    /**
     * 构造函数
     *
     * @param code  步骤返回类型 Code
     * @param label 步骤返回类型说明
     */
    AppStepResponseTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 获取步骤返回类型选项列表
     *
     * @return 步骤返回类型选项列表
     */
    public static List<Option> options() {
        return Arrays.stream(values())
                .filter(item -> !item.equals(IMAGE))
                .map(AppStepResponseTypeEnum::option)
                .collect(Collectors.toList());
    }

    /**
     * 获取步骤返回类型选项
     *
     * @param typeEnum 获取步骤返回类型选项枚举
     * @return 步骤返回样式选项
     */
    public static Option option(AppStepResponseTypeEnum typeEnum) {
        Option option = new Option();
        option.setLabel(typeEnum.getLabel());
        option.setValue(typeEnum.name());
        return option;
    }
}
