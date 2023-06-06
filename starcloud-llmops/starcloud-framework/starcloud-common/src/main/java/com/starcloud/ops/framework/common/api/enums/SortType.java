package com.starcloud.ops.framework.common.api.enums;

import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public enum SortType {

    /**
     * 升序
     */
    ASC("asc", "升序"),

    /**
     * 降序
     */
    DESC("desc", "降序");

    /**
     * 排序code
     */
    @Getter
    private final String code;

    /**
     * 排序描述
     */
    @Getter
    private final String desc;

    SortType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
