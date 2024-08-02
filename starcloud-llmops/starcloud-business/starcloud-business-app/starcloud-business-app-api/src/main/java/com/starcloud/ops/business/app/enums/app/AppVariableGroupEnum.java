package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 变量分组
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Getter
public enum AppVariableGroupEnum implements IEnumable<Integer> {

    /**
     * 变量类型为
     */
    SYSTEM(0, "系统变量"),

    /**
     * 高级变量
     */
    ADVANCED(1, "高级变量"),

    /**
     * 参数变量
     */
    PARAMS(1, "通用变量"),

    /**
     * 模型变量
     */
    MODEL(2, "模型变量");

    /**
     * 变量组Code
     */
    private final Integer code;

    /**
     * 变量组说明
     */
    private final String label;

    /**
     * 构造函数
     *
     * @param code  变量组 Code
     * @param label 变量组说明
     */
    AppVariableGroupEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    public static List<Option> options() {
        return Arrays.stream(values())
                .filter(item -> !item.equals(MODEL))
                .map(AppVariableGroupEnum::option)
                .collect(Collectors.toList());
    }

    public static Option option(AppVariableGroupEnum groupEnum) {
        Option option = new Option();
        option.setLabel(groupEnum.getLabel());
        option.setValue(groupEnum.name());
        return option;
    }
}
