package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppSceneEnum implements IEnumable<Integer> {

    /**
     * 生成内容/图片等应用
     */
    WEB_ADMIN(1, "WEB_ADMIN"),

    /**
     * 聊天应用
     */
    WEB_MARKET(2, "WEB_MARKET");

    /**
     * 应用类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 应用类型说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  枚举值
     * @param label 枚举描述
     */
    AppSceneEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
