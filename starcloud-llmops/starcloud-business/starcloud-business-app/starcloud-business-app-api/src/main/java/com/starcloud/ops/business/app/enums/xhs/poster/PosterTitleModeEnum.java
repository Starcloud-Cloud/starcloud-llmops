package com.starcloud.ops.business.app.enums.xhs.poster;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Getter
public enum PosterTitleModeEnum implements IEnumable<Integer> {

    DEFAULT(1, "默认"),

    AI(2, "AI生成");

    /**
     * 类型编码
     */
    private final Integer code;

    /**
     * 类型名称
     */
    private final String label;


    PosterTitleModeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
