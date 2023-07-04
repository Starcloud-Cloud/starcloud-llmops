package com.starcloud.ops.business.log.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-04
 */
public enum LogUnlikeReasonEnum implements IEnumable<Integer> {

    /**
     * 内容不健康
     */
    HARMFUL(1, "内容不健康", "This is harmful / unsafe"),

    /**
     * 内容不正确
     */
    NO_TRUE(2, "内容不正确", "This isn't true"),

    /**
     * 内容不实用
     */
    NOT_HELPFUL(3, "内容不实用", "This isn't helpful"),
    ;

    /**
     * 不喜欢原因编码
     */
    @Getter
    private final Integer code;

    /**
     * 不喜欢原因描述
     */
    @Getter
    private final String label;

    /**
     * 不喜欢原因描述英文
     */
    private final String labelEn;

    /**
     * 构造方法
     *
     * @param code  不喜欢原因
     * @param label 不喜欢原因
     */
    LogUnlikeReasonEnum(Integer code, String label, String labelEn) {
        this.code = code;
        this.label = label;
        this.labelEn = labelEn;
    }
}
