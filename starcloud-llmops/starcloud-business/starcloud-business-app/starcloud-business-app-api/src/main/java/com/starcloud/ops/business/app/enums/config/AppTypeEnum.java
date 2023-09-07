package com.starcloud.ops.business.app.enums.config;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AppTypeEnum {

    APP(0, "我的应用"),

    MARKET(1, "应用市场"),

    ;

    /**
     * 应用类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 应用类型说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  应用类型 Code
     * @param label 应用类型说明
     */
    AppTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }


    public static List<Integer> allType() {
        return Arrays.stream(AppTypeEnum.values()).map(AppTypeEnum::getCode).collect(Collectors.toList());
    }
}
