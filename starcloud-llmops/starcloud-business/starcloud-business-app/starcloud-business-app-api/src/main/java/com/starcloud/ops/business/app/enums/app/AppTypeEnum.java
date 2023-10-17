package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 应用类型, 0：系统推荐应用，1：我的应用，2：下载应用
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@SuppressWarnings("unused")
@Getter
public enum AppTypeEnum implements IEnumable<Integer> {

    /**
     * 系统应用
     */
    SYSTEM(0, "系统应用"),

    /**
     * 普通应用
     */
    COMMON(1, "普通应用"),

    ;

    /**
     * 应用类型Code
     */
    private final Integer code;

    /**
     * 应用类型说明
     */
    private final String label;

    /**
     * 构造函数
     *
     * @param code  应用类型 Code
     * @param label 应用类型说明
     */
    AppTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
