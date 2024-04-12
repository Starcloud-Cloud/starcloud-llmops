package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 变量样式枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Getter
public enum AppVariableStyleEnum implements IEnumable<Integer> {

    /**
     * 输入框样式
     */
    INPUT(0, "输入框"),

    /**
     * 文本框样式
     */
    TEXTAREA(1, "文本框"),

    /**
     * JSON样式
     */
    JSON(2, "JSON"),

    /**
     * 下拉框样式
     */
    SELECT(3, "下拉框"),

    /**
     * 单选框
     */
    RADIO(4, "单选框"),

    /**
     * 复选框
     */
    CHECKBOX(5, "复选框"),

    /**
     * 标签框
     */
    TAG_BOX(6, "标签框"),

    /**
     * 素材库
     */
    MATERIAL(7, "素材库"),

    /**
     * 图片
     */
    IMAGE(8, "图片");

    /**
     * 变量样式Code
     */
    private final Integer code;

    /**
     * 变量样式说明
     */
    private final String label;

    /**
     * 构造函数
     *
     * @param code  变量样式 Code
     * @param label 变量样式说明
     */
    AppVariableStyleEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    public static List<Option> options() {
        return Arrays.stream(values())
                .filter(item -> !item.equals(IMAGE))
                .map(AppVariableStyleEnum::option)
                .collect(Collectors.toList());
    }

    public static Option option(AppVariableStyleEnum styleEnum) {
        Option option = new Option();
        option.setLabel(styleEnum.getLabel());
        option.setValue(styleEnum.name());
        return option;
    }
}
