package com.starcloud.ops.business.app.enums.xhs.content;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Getter
public enum CreativeContentTypeEnum implements IEnumable<Integer> {

    /**
     * 所有生成，图片，内容
     */
    ALL(0, "所有生成"),

    /**
     * 内容生成
     */
    CONTENT(1, "内容生成"),

    /**
     * 图片生成
     */
    IMAGE(2, "图片生成");

    /**
     * 类型编码
     */
    private final Integer code;

    /**
     * 类型说明
     */
    private final String label;

    CreativeContentTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
