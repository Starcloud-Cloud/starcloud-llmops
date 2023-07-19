package com.starcloud.ops.business.app.enums.vsearch;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-14
 */
public enum ImageSizeEnum implements IEnumable<String> {

    SIZE_896_512("896x512", "7:4", "896x512", "896x512"),

    SIZE_768_512("768x512", "3:2", "768x512", "768x512"),

    SIZE_683_512("683x512", "4:3", "683x512", "683x512"),

    SIZE_640_512("640x512", "5:4", "640x512", "640x512"),

    SIZE_512_512("512x512", "1:1", "512x512", "512x512"),

    SIZE_512_640("512x640", "4:5", "512x640", "512x640"),

    SIZE_512_683("512x683", "3:4", "512x683", "512x683"),

    SIZE_512_768("512x768", "2:3", "512x768", "512x768"),

    SIZE_512_896("512x896", "4:7", "512x896", "512x896"),

    ;

    /**
     * 枚举code
     */
    @Getter
    private final String code;

    /**
     * 枚举scale
     */
    @Getter
    private final String scale;

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

    ImageSizeEnum(String code, String scale, String label, String description) {
        this.code = code;
        this.scale = scale;
        this.label = label;
        this.description = description;
    }
}
