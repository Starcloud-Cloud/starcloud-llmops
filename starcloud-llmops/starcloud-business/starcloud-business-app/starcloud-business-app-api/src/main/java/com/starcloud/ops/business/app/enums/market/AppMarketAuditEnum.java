package com.starcloud.ops.business.app.enums.market;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 应用市场审核步骤类型枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-14
 */
@SuppressWarnings("unused")
public enum AppMarketAuditEnum {

    /**
     * 未审核
     */
    PENDING(0, "未审核"),

    /**
     * 审核通过
     */
    APPROVED(1, "审核通过"),

    /**
     * 审核不通过
     */
    REJECTED(2, "审核不通过");

    /**
     * 步骤类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 步骤类型说明
     */
    @Getter
    private final String message;

    private static final List<AppMarketAuditEnum> SUPPORTS = Arrays.asList(AppMarketAuditEnum.APPROVED, AppMarketAuditEnum.REJECTED);

    /**
     * 构造函数
     *
     * @param code    步骤类型 Code
     * @param message 步骤类型说明
     */
    AppMarketAuditEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 判断是否支持
     *
     * @param code 步骤类型 Code
     * @return 是否支持
     */
    public static Boolean isSupported(Integer code) {
        return SUPPORTS.stream().anyMatch(item -> item.getCode().equals(code));
    }
}
