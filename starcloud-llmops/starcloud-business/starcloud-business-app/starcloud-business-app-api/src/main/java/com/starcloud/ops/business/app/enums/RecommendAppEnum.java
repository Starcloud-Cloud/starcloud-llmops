package com.starcloud.ops.business.app.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-29
 */
@Getter
public enum RecommendAppEnum implements IEnumable<Integer> {

    /**
     * 默认生成文本步骤的唯一标识
     */
    GENERATE_TEXT(1, "生成文本", "Generate Text"),

    /**
     * 默认生成文章步骤的唯一标识
     */
    GENERATE_ARTICLE(2, "生成文章", "Generate Article"),

    /**
     * 聊天机器人的唯一标识
     */
    CHAT_ROBOT(3, "聊天机器人", "Chat Robot"),

    /**
     * 默认生成图片步骤的唯一标识
     */
    GENERATE_IMAGE(4, "AI自由绘图", "Generate Image"),

    /**
     * 图片放大的唯一标识
     */
    UPSCALING_IMAGE(5, "图片高清放大", "Upscaling Image"),

    /**
     * 去除背景的唯一标识
     */
    REMOVE_BACKGROUND_IMAGE(6, "智能抠图", "Remove Background Image"),

    /**
     * 替换背景的唯一标识
     */
    REPLACE_BACKGROUND_IMAGE(7, "图片替换背景", "Replace Background Image"),

    /**
     * 去除文字的唯一标识
     */
    REMOVE_TEXT_IMAGE(8, "智能去文字", "Remove Text Image"),

    /**
     * 草图生成图片的唯一标识
     */
    SKETCH_TO_IMAGE(9, "轮廓生图", "Sketch To Image"),

    /**
     * 图片变体的唯一标识
     */
    VARIANTS_IMAGE(10, "图片裂变", "Variants Image");

    /**
     * 图片生成的应用
     */
    public static final List<String> IMAGE_APP = Arrays.asList(
            GENERATE_IMAGE.name(),
            UPSCALING_IMAGE.name(),
            REMOVE_BACKGROUND_IMAGE.name(),
            REPLACE_BACKGROUND_IMAGE.name(),
            REMOVE_TEXT_IMAGE.name(),
            SKETCH_TO_IMAGE.name(),
            VARIANTS_IMAGE.name()
    );
    /**
     * code
     */
    private final Integer code;

    /**
     * 标签
     */
    private final String label;

    /**
     * 英文标签
     */
    private final String labelEn;

    /**
     * 构造函数
     *
     * @param code    code
     * @param label   标签
     * @param labelEn 英文标签
     */
    RecommendAppEnum(Integer code, String label, String labelEn) {
        this.code = code;
        this.label = label;
        this.labelEn = labelEn;
    }
}
