package com.starcloud.ops.business.app.recommend.enums;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 写作基调枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-27
 */
public enum WritingToneEnum implements IEnumable<String> {

    /**
     * 中性
     */
    NEUTRAL("Neutral", "中性"),

    /**
     * 正式
     */
    FORMAL("Formal", "正式"),

    /**
     * 非正式的
     */
    INFORMAL("Informal", "非正式"),

    /**
     * 自信
     */
    ASSERTIVE("Assertive", "自信"),

    /**
     * 欢快
     */
    CHEERFUL("Cheerful", "欢快"),

    /**
     * 幽默
     */
    HUMOROUS("Humorous", "幽默"),

    /**
     * 鼓舞
     */
    INSPIRATIONAL("Inspirational", "鼓舞"),

    /**
     * 专业
     */
    PROFESSIONAL("Professional", "专业"),

    /**
     * 凝聚
     */
    Confluent("Confluent", "凝聚"),

    /**
     * 感性
     */
    Emotional("Emotional", "感性"),

    /**
     * 劝说
     */
    Persuasive("Persuasive", "劝说"),

    /**
     * 支持
     */
    Supportive("Supportive", "支持"),

    /**
     * 讽刺
     */
    SARCASTIC("Sarcastic", "讽刺"),

    /**
     * 傲慢
     */
    CONDESCENDING("Condescending", "傲慢"),

    /**
     * 怀疑
     */
    SKEPTICAL("Skeptical", "怀疑"),

    /**
     * 叙事
     */
    NARRATIVE("Narrative", "叙事"),

    /**
     * 新闻
     */
    Journalistic("Journalistic", "新闻"),
    ;

    @Getter
    private final String code;

    @Getter
    private final String label;

    /**
     * 构造方法
     *
     * @param code  枚举编码
     * @param label 枚举标签
     */
    WritingToneEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 获取所有选项
     *
     * @return 所有选项
     */
    public static List<Option> ofOptions() {
        WritingToneEnum[] values = WritingToneEnum.values();
        return Arrays.stream(values).map(tone -> {
            Option option = new Option();
            option.setValue(tone.getCode());
            Locale locale = LocaleContextHolder.getLocale();
            if (Locale.CHINA.equals(locale)) {
                option.setLabel(tone.getLabel());
            } else {
                option.setLabel(tone.getCode());
            }
            return option;
        }).collect(Collectors.toList());
    }
}
