package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-21
 */
public enum LanguageEnum implements IEnumable<String> {

    /**
     * 简体中文
     */
    ZH_CN("zh_CN", "中文", "Chinese"),

    /**
     * 英语
     */
    EN_US("en_US", "英语", "English"),

    /**
     * 俄语
     */
    RU_RU("ru_RU", "俄语", "Russian"),

    /**
     * 法语
     */
    FR_FR("fr_FR", "法语", "French"),

    /**
     * 德语
     */
    DE_DE("de_DE", "德语", "German"),

    /**
     * 意大利语
     */
    IT_IT("it_IT", "意大利语", "Italian"),

    /**
     * 葡萄牙语
     */
    PT_PT("pt_PT", "葡萄牙语", "Portuguese"),

    /**
     * 西班牙语
     */
    ES_ES("es_ES", "西班牙语", "Spanish"),

    /**
     * 波兰语
     */
    PL_PL("pl_PL", "波兰语", "Polish"),

    /**
     * 荷兰语
     */
    NL_NL("nl_NL", "荷兰语", "Dutch"),

    /**
     * 瑞典语
     */
    SV_SE("sv_SE", "瑞典语", "Swedish"),

    /**
     * 丹麦语
     */
    DA_DK("da_DK", "丹麦语", "Danish"),

    /**
     * 挪威语
     */
    NO_NO("no_NO", "挪威语", "Norwegian"),

    /**
     * 芬兰语
     */
    FI_FI("fi_FI", "芬兰语", "Finnish"),

    /**
     * 日语
     */
    JA_JP("ja_JP", "日语", "Japanese"),

    /**
     * 韩语
     */
    KO_KR("ko_KR", "韩语", "Korean"),

    /**
     * 阿拉伯语
     */
    AR_SA("ar_SA", "阿拉伯语", "Arabic"),

    /**
     * 土耳其语
     */
    TR_TR("tr_TR", "土耳其语", "Turkish"),
    ;

    @Getter
    private final String code;

    @Getter
    private final String label;

    @Getter
    private final String labelEn;

    LanguageEnum(String code, String label, String labelEn) {
        this.code = code;
        this.label = label;
        this.labelEn = labelEn;
    }

    /**
     * 获取语言列表,使用 code 作为 value 值
     *
     * @return 语言列表
     */
    public static List<Option> languageList() {
        return languageList(false);
    }

    /**
     * 获取语言列表
     *
     * @param isUseLabelEnAsOptionValue 是否使用英文标签作为选项的 value 值，true 为使用，false 为使用 code 作为 value 值
     * @return 语言列表
     */
    public static List<Option> languageList(boolean isUseLabelEnAsOptionValue) {
        LanguageEnum[] values = LanguageEnum.values();
        return Arrays.stream(values).map(language -> {
            Option option = new Option();
            if (isUseLabelEnAsOptionValue) {
                option.setValue(language.getLabelEn());
            } else {
                option.setValue(language.getCode());
            }
            Locale locale = LocaleContextHolder.getLocale();
            if (Locale.CHINA.equals(locale)) {
                option.setLabel(language.getLabel());
            } else {
                option.setLabel(language.getLabelEn());
            }
            return option;
        }).collect(Collectors.toList());
    }
}
