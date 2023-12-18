package com.starcloud.ops.business.app.enums.market;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Listing 生成类型枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-26
 */
@Getter
public enum AppMarketTagTypeEnum implements IEnumable<Integer> {

    /**
     * 优化图片生成提示词
     */
    OPTIMIZE_IMAGE_PROMPT(1, "优化图片生成提示词", Arrays.asList("Optimize Prompt", "Image")),

    /**
     * 优化对话人物简介
     */
    OPTIMIZE_PROMPT_CHAR_DESC(2, "优化对话人物简介", Arrays.asList("Optimize Prompt", "Chat", "Desc")),

    /**
     * 优化机器人欢迎语
     */
    OPTIMIZE_PROMPT_CHAR_WELCOME(3, "优化机器人欢迎语", Arrays.asList("Optimize Prompt", "Chat", "Welcome")),

    /**
     * 优化机器人角色描述
     */
    OPTIMIZE_PROMPT_CHAR_Role(3, "优化机器人角色描述", Arrays.asList("Optimize Prompt", "Chat", "Role")),

    /**
     * 小红书文案生成
     */
    XIAO_HONG_SHU_WRITING(1, "小红书文案生成", Arrays.asList("Writing", "小红书")),

    /**
     * 小红书文案干货文生成
     */
    XIAO_HONG_SHU_PRACTICAL_WRITING(1, "小红书文案生成", Arrays.asList("Writing", "小红书", "干货")),

    /**
     * Listing标题生成
     */
    LISTING_TITLE(10, "Listing标题生成", Arrays.asList("Listing", "Title")),

    /**
     * Listing五点描述生成
     */
    LISTING_BULLET_POINT(11, "Listing五点描述生成", Arrays.asList("Listing", "BulletPoint")),

    /**
     * Listing产品描述生成
     */
    LISTING_PRODUCT_DESCRIPTION(12, "Listing产品描述生成", Arrays.asList("Listing", "ProductDescription"));

    /**
     * 枚举值
     */
    private final Integer code;

    /**
     * 枚举描述
     */
    private final String label;

    /**
     * 枚举标签
     */
    private final List<String> tags;

    /**
     * 根据枚举值获取枚举
     *
     * @param name 枚举值
     * @return 枚举
     */
    public static AppMarketTagTypeEnum of(String name) {
        for (AppMarketTagTypeEnum value : AppMarketTagTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 构造方法
     *
     * @param code  枚举值
     * @param label 枚举描述
     * @param tags  枚举标签
     */
    AppMarketTagTypeEnum(Integer code, String label, List<String> tags) {
        this.code = code;
        this.label = label;
        this.tags = tags;
    }

}
