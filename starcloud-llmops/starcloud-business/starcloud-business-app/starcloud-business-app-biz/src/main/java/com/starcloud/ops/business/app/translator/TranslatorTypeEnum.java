package com.starcloud.ops.business.app.translator;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
public enum TranslatorTypeEnum implements IEnumable<Integer> {

    /**
     * 阿里云翻译
     */
    ALI_YUN(1, "阿里云翻译"),

    /**
     * 华为翻译
     */
    HUA_WEI(2, "华为翻译"),

    /**
     * 百度翻译
     */
    BAI_DU(3, "百度翻译"),

    /**
     * 有道翻译
     */
    YOU_DAO(4, "有道翻译"),

    /**
     * 讯飞翻译
     */
    XUN_FEI(5, "讯飞翻译"),

    /**
     * 谷歌翻译
     */
    GOOGLE(6, "谷歌翻译"),

    /**
     * DeepL翻译
     */
    DEEPL(7, "DeepL翻译"),

    /**
     * 腾讯翻译
     */
    TENCENT(8, "腾讯翻译"),

    /**
     * 微软翻译
     */
    MICROSOFT(9, "微软翻译");


    @Getter
    private final Integer code;

    @Getter
    private final String label;

    TranslatorTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
