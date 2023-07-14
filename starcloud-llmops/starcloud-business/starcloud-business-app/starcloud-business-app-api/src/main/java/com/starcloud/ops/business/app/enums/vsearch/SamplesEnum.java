package com.starcloud.ops.business.app.enums.vsearch;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-14
 */
public enum SamplesEnum implements IEnumable<Integer> {

    ONE(1, "1", "1"),

    TWO(2, "2", "2"),

    THREE(3, "3", "3"),

    FOUR(4, "4", "4"),

    FIVE(5, "5", "5"),

    SIX(6, "6", "6"),

    SEVEN(7, "7", "7"),

    EIGHT(8, "8", "8"),

    ;

    /**
     * 枚举code
     */
    @Getter
    private final Integer code;

    /**
     * 枚举label
     */
    @Getter
    private final String label;

    /**
     * 枚举描述
     */
    @Getter
    private final String description;

    SamplesEnum(Integer code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }
}
