package com.starcloud.ops.business.app.enums.market;

import lombok.Getter;

/**
 * 是否免费枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-15
 */
@SuppressWarnings("unused")
public enum AppMarketFreeEnum {

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
    private final String message;

    /**
     * 构造函数
     *
     * @param code    是否免费Code
     * @param message 是否免费说明
     */
    AppMarketFreeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
