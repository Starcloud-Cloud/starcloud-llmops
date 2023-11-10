package com.starcloud.ops.business.listing.enums;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 亚马逊 Listing 语言，包含各个亚马逊站点的语言
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-30
 */
@Getter
public enum ListingLanguageEnum implements IEnumable<String> {

    /**
     * 英语美国
     */
    ENGLISH_US("English (United States)", "英语美国", 1),

    /**
     * 英语英国
     */
    ENGLISH_UK("English (United Kingdom)", "英语英国", 2),

    /**
     * 简体中文
     */
    CHINESE_SIMPLIFIED("Chinese Simplified", "简体中文", 3),

    /**
     * 繁体中文(台湾)
     */
    CHINESE_TRADITIONAL_TW("Chinese Traditional (Taiwan)", "繁体中文(台湾)", 4),

    /**
     * 繁体中文(香港)
     */
    CHINESE_TRADITIONAL_HK("Chinese Traditional (Hong Kong)", "繁体中文(香港)", 5),

    /**
     * 俄语
     */
    RUSSIAN("Russian", "俄语", 6),

    /**
     * 法语
     */
    FRENCH("French", "法语", 7),

    /**
     * 德语
     */
    GERMAN("German", "德语", 8),

    /**
     * 意大利语
     */
    ITALIAN("Italian", "意大利语", 9),

    /**
     * 荷兰语
     */
    DUTCH("Dutch", "荷兰语", 10),

    /**
     * 波兰语
     */
    POLISH("Polish", "波兰语", 11),

    /**
     * 瑞典语
     */
    SWEDISH("Swedish", "瑞典语", 12),

    /**
     * 西班牙语
     */
    SPANISH("Spanish", "西班牙语", 13),

    /**
     * 葡萄牙语
     */
    PORTUGUESE("Portuguese", "葡萄牙语", 14),

    /**
     * 日语
     */
    JAPANESE("Japanese", "日语", 15),

    /**
     * 韩语
     */
    KOREAN("Korean", "韩语", 16),

    /**
     * 印度语
     */
    HINDI("Hindi", "印度语", 17),

    /**
     * 阿拉伯语
     */
    ARABIC("Arabic", "阿拉伯语", 18),

    /**
     * 土耳其语
     */
    TURKISH("Turkish", "土耳其语", 19),

    ;

    /**
     * 语言编码
     */
    private final String code;

    /**
     * 语言标签
     */
    private final String label;

    /**
     * 排序
     */
    private final Integer sort;

    /**
     * 构造方法
     *
     * @param code  语言编码
     * @param label 语言标签
     * @param sort  排序
     */
    ListingLanguageEnum(String code, String label, Integer sort) {
        this.code = code;
        this.label = label;
        this.sort = sort;
    }

    /**
     * 获取Option列表
     *
     * @return Option列表
     */
    public static List<Option> options() {
        return Arrays.stream(ListingLanguageEnum.values())
                .sorted(Comparator.comparingInt(ListingLanguageEnum::getSort))
                .map(item -> Option.of(item.getLabel(), item.getCode())).collect(Collectors.toList());
    }
}
