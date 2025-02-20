package com.starcloud.ops.business.app.enums.favorite;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum AppFavoriteTypeEnum implements IEnumable<String> {

    APP_MARKET("APP_MARKET", "应用市场"),

    TEMPLATE_MARKET("TEMPLATE_MARKET", "模板市场"),
    ;

    /**
     * 应用类型Code
     */
    private final String code;

    /**
     * 应用类型说明
     */
    private final String label;
}
