package com.starcloud.ops.business.app.api.verification;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 校验消息，用于校验失败时返回给前端的消息
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Getter
public enum VerificationType implements IEnumable<Integer> {

    /**
     * 应用类型错误(我的应用，应用市场)
     */
    APP(1, "应用错误"),

    /**
     * 聊天类型错误
     */
    CHAT(2, "聊天"),

    /**
     * 图片类型错误
     */
    IMAGE(3, "图片错误"),

    /**
     * 步骤类型错误
     */
    STEP(4, "步骤错误"),

    /**
     * 创作类型错误
     */
    CREATIVE(5, "创作错误"),

    /**
     * 素材库类型错误
     */
    MATERIAL(6, "素材库错误");

    /**
     * 类型CODE
     */
    private final Integer code;

    /**
     * 类型名称
     */
    private final String label;

    /**
     * 构造函数
     *
     * @param code  类型CODE
     * @param label 类型名称
     */
    VerificationType(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
