package com.starcloud.ops.business.log.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-04
 */
public enum LogFeedbackTypeEnum implements IEnumable<Integer> {

    /**
     * 点赞
     */
    LIKE(1, "点赞"),

    /**
     * 踩
     */
    UNLIKE(2, "踩");

    /**
     * 类型编码
     */
    @Getter
    private final Integer code;

    /**
     * 类型描述
     */
    @Getter
    private final String label;

    LogFeedbackTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
