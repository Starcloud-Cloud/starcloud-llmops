package com.starcloud.ops.business.app.enums.limit;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-30
 */
@Getter
public enum LimitByEnum implements IEnumable<Integer> {

    /**
     * 根据应用进行限流
     */
    APP(1, "根据应用进行限流"),

    /**
     * 根据应用和用户进行限流
     */
    USER(2, "根据应用和用户进行限流)"),

    /**
     * 根据应用和团队进行限流
     */
    TEAM(3, "根据应用和团队进行限流"),

    /**
     * 广告
     */
    ADVERTISING(4, "广告");

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 标签
     */
    private final String label;

    /**
     * 构造方法
     *
     * @param code  编码
     * @param label 标签
     */
    LimitByEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
