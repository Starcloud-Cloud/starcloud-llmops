package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppModelEnum implements IEnumable<Integer> {

    /**
     * 生成内容/图片等应用
     */
    COMPLETION(1, "生成式应用"),

    /**
     * 聊天应用
     */
    CHAT(2, "聊天式应用"),

    /**
     * 基础图片生成模式
     */
    BASE_GENERATE_IMAGE(3, "基础图片生成模式，文字生成图片，图片生成图片"),

    ;

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
    AppModelEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
