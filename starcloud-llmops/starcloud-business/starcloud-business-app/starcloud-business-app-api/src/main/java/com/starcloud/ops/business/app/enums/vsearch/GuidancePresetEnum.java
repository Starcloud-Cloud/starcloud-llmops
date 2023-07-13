package com.starcloud.ops.business.app.enums.vsearch;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 指导预设
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-10
 */
public enum GuidancePresetEnum implements IEnumable<Integer> {

    /**
     * NONE
     */
    NONE(0, "GUIDANCE_PRESET_NONE_LABEL", "GUIDANCE_PRESET_NONE_DESCRIPTION", ""),

    /**
     * SIMPLE
     */
    SIMPLE(1, "GUIDANCE_PRESET_SIMPLE_LABEL", "GUIDANCE_PRESET_SIMPLE_DESCRIPTION", ""),

    /**
     * FAST_BLUE
     */
    FAST_BLUE(2, "GUIDANCE_PRESET_FAST_BLUE_LABEL", "GUIDANCE_PRESET_FAST_BLUE_DESCRIPTION", ""),

    /**
     * FAST_GREEN
     */
    FAST_GREEN(3, "GUIDANCE_PRESET_FAST_GREEN_LABEL", "GUIDANCE_PRESET_FAST_GREEN_DESCRIPTION", ""),

    /**
     * SLOW
     */
    SLOW(4, "GUIDANCE_PRESET_SLOW_LABEL", "GUIDANCE_PRESET_SLOW_DESCRIPTION", ""),

    /**
     * SLOWER
     */
    SLOWER(5, "GUIDANCE_PRESET_SLOWER_LABEL", "GUIDANCE_PRESET_SLOWER_DESCRIPTION", ""),

    /**
     * SLOWEST
     */
    SLOWEST(6, "GUIDANCE_PRESET_SLOWEST_LABEL", "GUIDANCE_PRESET_SLOWEST_DESCRIPTION", "");

    /**
     * 枚举code
     */
    @Getter
    private final Integer code;

    /**
     * 枚举label
     */
    @Getter
    private final String label;

    /**
     * 枚举描述
     */
    @Getter
    private final String description;

    /**
     * 枚举图片
     */
    @Getter
    private final String image;

    /**
     * 构造方法
     *
     * @param code        枚举 code
     * @param label       枚举 label
     * @param description 枚举描述
     * @param image       枚举图片
     */
    GuidancePresetEnum(Integer code, String label, String description, String image) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.image = image;
    }
}
