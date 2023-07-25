package com.starcloud.ops.business.app.translator;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-25
 */
public enum LanguageEnum implements IEnumable<String> {

    /**
     * 中文
     */
    ZH_CN("zh", "中文", "Chinese"),

    /**
     * 英文
     */
    EN_US("en", "英文", "English"),

    /**
     * 俄语
     */
    RU_RU("ru", "俄语", "Russian"),

    /**
     * 法语
     */
    FR_FR("fr", "法语", "French"),

    /**
     * 德语
     */
    DE_DE("de", "德语", "German"),

    /**
     * 西班牙语
     */
    ES_ES("es", "西班牙语", "Spanish"),

    /**
     * 葡萄牙语
     */
    PT_PT("pt", "葡萄牙语", "Portuguese"),

    /**
     * 日语
     */
    JA_JP("ja", "日文", "Japanese"),

    /**
     * 韩语
     */
    KO_KR("ko", "韩语", "Korean"),
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
}
