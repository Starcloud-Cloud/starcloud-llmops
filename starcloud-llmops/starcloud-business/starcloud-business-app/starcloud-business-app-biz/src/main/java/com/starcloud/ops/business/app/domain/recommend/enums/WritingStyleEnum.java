package com.starcloud.ops.business.app.domain.recommend.enums;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 写作风格枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-27
 */
public enum WritingStyleEnum implements IEnumable<String> {

    /**
     * 丰富性风格
     */
    INFORMATIVE("Informative", "丰富性风格"),

    /**
     * 描述性风格
     */
    DESCRIPTIVE("Descriptive", "描述性风格"),

    /**
     * 创造性风格
     */
    CREATIVE("Creative", "创造性风格"),

    /**
     * 叙事性风格
     */
    NARRATIVE("Narrative", "叙事性风格"),

    /**
     * 说明性风格
     */
    PERSUASIVE("Persuasive", "劝说性风格"),

    /**
     * 反思性风格
     */
    REFLECTIVE("Reflective", "反思性风格"),

    /**
     * 论证性风格
     */
    ARGUMENTATIVE("Argumentative", "论证性风格"),

    /**
     * 分析性风格
     */
    ANALYTICAL("Analytical", "分析性风格"),

    /**
     * 评价性风格
     */
    Evaluative("Evaluative", "评价性风格"),

    /**
     * 新闻性风格
     */
    JOURNALISTIC("Journalistic", "新闻性风格"),

    /**
     * 技术性风格
     */
    TECHNICAL("Technical", "技术性风格"),
    ;

    @Getter
    private final String code;

    @Getter
    private final String label;

    /**
     * 构造方法
     *
     * @param code  写作风格编码
     * @param label 写作风格名称
     */
    WritingStyleEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 获取写作风格列表
     *
     * @return 写作风格列表
     */
    public static List<Option> ofOptions() {
        WritingStyleEnum[] values = WritingStyleEnum.values();
        return Arrays.stream(values).map(style -> {
            Option option = new Option();
            option.setValue(style.getCode());
            Locale locale = LocaleContextHolder.getLocale();
            if (Locale.CHINA.equals(locale)) {
                option.setLabel(style.getLabel());
            } else {
                option.setLabel(style.getCode());
            }
            return option;
        }).collect(Collectors.toList());
    }
}

