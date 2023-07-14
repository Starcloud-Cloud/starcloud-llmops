package com.starcloud.ops.business.app.enums.vsearch;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-14
 */
public enum ImageSizeEnum implements IEnumable<String> {

    SIZE_512_512("512x512", "1:1", "512x512", "512x512"),

    SIZE_768_768("768x768", "1:1", "768x768", "768x768"),

    SIZE_1024_1024("1024x1024", "1:1", "1024x1024", "1024x1024"),

    SIZE_512_768("512x768", "2:3", "512x768", "512x768"),

    SIZE_768_1152("768x1152", "2:3", "768x1152", "768x1152"),

    SIZE_576_768("576x768", "3:4", "576x768", "576x768"),

    SIZE_768_1024("768x1024", "3:4", "768x1024", "768x1024"),

    SIZE_504_896("504x896", "9:16", "504x896", "504x896"),

    SIZE_720_1280("720x1280", "9:16", "720x1280", "720x1280"),

    SIZE_768_512("768x512", "3:2", "768x512", "768x512"),

    SIZE_1152_768("1152x768", "3:2", "1152x768", "1152x768"),

    SIZE_768_576("768x576", "4:3", "768x576", "768x576"),

    SIZE_1024_768("1024x768", "4:3", "1024x768", "1024x768"),

    SIZE_896_504("896x504", "16:9", "896x504", "896x504"),

    SIZE_1280_720("1280x720", "16:9", "1280x720", "1280x720"),
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
