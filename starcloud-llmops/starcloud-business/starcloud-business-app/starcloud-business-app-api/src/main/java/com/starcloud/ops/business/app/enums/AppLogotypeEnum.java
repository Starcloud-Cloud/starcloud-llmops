package com.starcloud.ops.business.app.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 版标识, 区分自定义模版和每一种具体的系统模版，所有的模版的具体类型都基于此标识，不同的标识，模版的具体配置（步骤，变量，场景等）会有所不同。
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@SuppressWarnings("unused")
public enum AppLogotypeEnum {

    /**
     * 自定义模版：用户可以自定义模版的步骤
     */
    CUSTOM(0, "自定义模版：用户可以自定义模版的步骤"),

    /**
     * 系统模版：生成文本模版
     */
    TEXT(1, "系统模版：生成文本模版"),

    /**
     * 系统模版：生成图片模版
     */
    IMAGE(2, "系统模版：生成图片模版"),

    /**
     * 系统模版：根据文本生成图片模版
     */
    IMAGE_BY_TEXT(3, "系统模版：根据文本生成图片模版"),

    /**
     * 系统模版：文章模版
     */
    ARTICLE(4, "系统模版：文章模版"),

    /**
     * 系统模版：产品报告模版
     */
    PRODUCT_REPORT(5, "系统模版：产品报告模版"),

    /**
     * 系统模版：文章标题模版
     */
    POST_TITLE(6, "系统模版：文章标题模版"),

    /**
     * 系统模版：文章摘要模版
     */
    POST_EXCERPT(7, "系统模版：文章摘要模版"),

    /**
     * 系统模版：翻译文本模版
     */
    TRANSLATE_TEXT(8, "系统模版：翻译文本模版"),

    /**
     * 系统模版：提升写作模版
     */
    IMPROVE_WRITING(9, "系统模版：提升写作模版"),

    /**
     * 系统模版：续写模版
     */
    CONTINUE_WRITING(10, "系统模版：续写模版"),

    /**
     * 系统模版：延长文本模版
     */
    MAKE_LONGER(11, "系统模版：延长文本模版"),

    /**
     * 系统模版：总结文本模版
     */
    SUMMARIZE_TEXT(12, "系统模版：总结文本模版"),

    /**
     * 系统模版：总结表格模版
     */
    SUMMARIZE_TABLE(13, "系统模版：总结表格模版"),

    /**
     * 系统模版：生成表格模版
     */
    GENERATE_TABLE(14, "系统模版：生成表格模版"),

    ;

    /**
     * 模版类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 模版类型说明
     */
    @Getter
    private final String message;

    /**
     * 用 Map 将枚举在初始化时候缓存，方便后续查询
     */
    private static final Map<String, AppLogotypeEnum> TEMPLATE_LOGOTYPE_CACHE = new ConcurrentHashMap<>();

    static {
        Arrays.stream(AppLogotypeEnum.values()).forEach(item -> TEMPLATE_LOGOTYPE_CACHE.put(item.name(), item));
    }

    /**
     * 构造函数
     *
     * @param code    模版类型 Code
     * @param message 模版类型说明
     */
    AppLogotypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据枚举名称获取枚举Code
     *
     * @param name 枚举名称
     *
     * @return 枚举Code
     */
    public static Integer getCodeByName(String name) {
        return getEnumByName(name).getCode();
    }

    /**
     * 根据枚举Code 获取枚举名称
     *
     * @param code 枚举 Code
     *
     * @return 枚举名称
     */
    public static String getNameByCode(Integer code) {
        for (AppLogotypeEnum type : TEMPLATE_LOGOTYPE_CACHE.values()) {
            if (type.getCode().equals(code)) {
                return type.name();
            }
        }
        throw new IllegalArgumentException("No enum constant " + AppLogotypeEnum.class.getCanonicalName() + ". When Code is: " + code);
    }

    /**
     * 根据枚举名称获取枚举
     *
     * @param name 枚举名称
     *
     * @return 模版类型
     */
    public static AppLogotypeEnum getEnumByName(String name) {
        if (TEMPLATE_LOGOTYPE_CACHE.containsKey(name)) {
            return TEMPLATE_LOGOTYPE_CACHE.get(name);
        }
        throw new IllegalArgumentException("No enum constant " + AppLogotypeEnum.class.getCanonicalName() + "." + name);
    }
}
