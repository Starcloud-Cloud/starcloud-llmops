package com.starcloud.ops.business.app.enums.vsearch;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-14
 */
@Getter
public enum MaskSourceEnum implements IEnumable<Integer> {

    /**
     * MASK_IMAGE_WHITE 将使用mask_image的白色像素作为蒙版，其中白色像素被完全替换，黑色像素保持不变
     */
    MASK_IMAGE_WHITE(1, "SOURCE_MASK_IMAGE_WHITE_LABEL", "SOURCE_MASK_IMAGE_WHITE_DESCRIPTION"),

    /**
     * MASK_IMAGE_BLACK 将使用mask_image的黑色像素作为蒙版，其中黑色像素被完全替换，白色像素保持不变
     */
    MASK_IMAGE_BLACK(2, "SOURCE_MASK_IMAGE_BLACK_LABEL", "SOURCE_MASK_IMAGE_BLACK_DESCRIPTION"),

    /**
     * INIT_IMAGE_ALPHA 将使用init_image的 Alpha 通道作为蒙版，其中完全透明像素被完全替换，完全不透明像素保持不变
     */
    INIT_IMAGE_ALPHA(3, "SOURCE_INIT_IMAGE_ALPHA_LABEL", "SOURCE_INIT_IMAGE_ALPHA_DESCRIPTION");

    /**
     * Code
     */
    private final Integer code;

    /**
     * 名称
     */
    private final String label;

    /**
     * 枚举描述
     */
    private final String description;

    /**
     * 构造方法
     *
     * @param code        枚举编码
     * @param label       枚举名称
     * @param description 枚举描述
     */
    MaskSourceEnum(Integer code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }
}
