package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 步骤返回样式枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Getter
public enum AppStepResponseStyleEnum implements IEnumable<Integer> {

    /**
     * 输入框样式
     */
    INPUT(0, "输入框"),

    /**
     * 文本框样式
     */
    TEXTAREA(1, "文本"),

    /**
     * JSON格式
     */
    JSON(2, "JSON"),

    /**
     * 图片样式
     */
    IMAGE(3, "图片");

    /**
     * 步骤返回样式Code
     */
    private final Integer code;

    /**
     * 步骤返回样式说明
     */
    private final String label;

    /**
     * 构造函数
     *
     * @param code  步骤返回样式 Code
     * @param label 步骤返回样式说明
     */
    AppStepResponseStyleEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 获取步骤返回样式选项列表
     *
     * @return 步骤返回样式选项列表
     */
    public static List<Option> options() {
        return Arrays.stream(values())
                .filter(item -> !item.equals(INPUT))
                .filter(item -> !item.equals(IMAGE))
                .map(AppStepResponseStyleEnum::option)
                .collect(Collectors.toList());
    }

    /**
     * 获取步骤返回样式选项
     *
     * @param styleEnum 获取步骤返回样式选项枚举
     * @return 步骤返回样式选项
     */
    public static Option option(AppStepResponseStyleEnum styleEnum) {
        Option option = new Option();
        option.setLabel(styleEnum.getLabel());
        option.setValue(styleEnum.name());
        return option;
    }
}
