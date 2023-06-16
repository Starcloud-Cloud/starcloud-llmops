package com.starcloud.ops.business.app.enums.market;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 是否免费枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-15
 */
@SuppressWarnings("unused")
public enum AppMarketFreeEnum implements IEnumable<Integer> {

    /**
     * 免费
     */
    FREE(0, "免费"),

    /**
     * 付费
     */
    PAY(1, "付费");

    /**
     * 是否免费Code
     */
    @Getter
    private final Integer code;

    /**
     * 是否免费说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  是否免费Code
     * @param label 是否免费说明
     */
    AppMarketFreeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
