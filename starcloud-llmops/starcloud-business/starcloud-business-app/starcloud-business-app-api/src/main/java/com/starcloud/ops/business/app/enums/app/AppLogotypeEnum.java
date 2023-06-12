package com.starcloud.ops.business.app.enums.app;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 版标识, 区分自定义应用和每一种具体的系统应用，所有的应用的具体类型都基于此标识，不同的标识，应用的具体配置（步骤，变量，场景等）会有所不同。
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@SuppressWarnings("unused")
public enum AppLogotypeEnum {

    /**
     * 自定义应用：用户可以自定义应用的步骤
     */
    CUSTOM(0, "自定义应用：用户可以自定义应用的步骤"),

    /**
     * 系统应用：生成文本应用
     */
    TEXT(1, "系统应用：生成文本应用"),

    /**
     * 系统应用：生成图片应用
     */
    IMAGE(2, "系统应用：生成图片应用"),

    /**
     * 系统应用：根据文本生成图片应用
     */
    IMAGE_BY_TEXT(3, "系统应用：根据文本生成图片应用"),

    /**
     * 系统应用：文章应用
     */
    ARTICLE(4, "系统应用：文章应用"),

    /**
     * 系统应用：产品报告应用
     */
    PRODUCT_REPORT(5, "系统应用：产品报告应用"),

    /**
     * 系统应用：文章标题应用
     */
    POST_TITLE(6, "系统应用：文章标题应用"),

    /**
     * 系统应用：文章摘要应用
     */
    POST_EXCERPT(7, "系统应用：文章摘要应用"),

    /**
     * 系统应用：翻译文本应用
     */
    TRANSLATE_TEXT(8, "系统应用：翻译文本应用"),

    /**
     * 系统应用：提升写作应用
     */
    IMPROVE_WRITING(9, "系统应用：提升写作应用"),

    /**
     * 系统应用：续写应用
     */
    CONTINUE_WRITING(10, "系统应用：续写应用"),

    /**
     * 系统应用：延长文本应用
     */
    MAKE_LONGER(11, "系统应用：延长文本应用"),

    /**
     * 系统应用：总结文本应用
     */
    SUMMARIZE_TEXT(12, "系统应用：总结文本应用"),

    /**
     * 系统应用：总结表格应用
     */
    SUMMARIZE_TABLE(13, "系统应用：总结表格应用"),

    /**
     * 系统应用：生成表格应用
     */
    GENERATE_TABLE(14, "系统应用：生成表格应用"),

    ;

    /**
     * 应用类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 应用类型说明
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
     * @param code    应用类型 Code
     * @param message 应用类型说明
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
     * @return 应用类型
     */
    public static AppLogotypeEnum getEnumByName(String name) {
        if (TEMPLATE_LOGOTYPE_CACHE.containsKey(name)) {
            return TEMPLATE_LOGOTYPE_CACHE.get(name);
        }
        throw new IllegalArgumentException("No enum constant " + AppLogotypeEnum.class.getCanonicalName() + "." + name);
    }
}
