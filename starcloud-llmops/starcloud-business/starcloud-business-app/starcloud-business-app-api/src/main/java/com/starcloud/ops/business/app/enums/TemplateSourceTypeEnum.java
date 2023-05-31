package com.starcloud.ops.business.app.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模版来源类型，表示模版的是从那个平台创建，或者下载的。比如 WrdPress ， Chrome插件等
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@SuppressWarnings("unused")
public enum TemplateSourceTypeEnum {

    /**
     * WordPress 插件
     */
    WORDPRESS_PLUGIN(0, "WordPress 插件"),

    /**
     * Chrome 插件
     */
    CHROME_PLUGIN(1, "Chrome 插件"),

    /**
     * Edge 插件
     */
    EDGE_PLUGIN(2, "Edge 插件"),

    /**
     * Firefox 插件
     */
    FIREFOX_PLUGIN(3, "Firefox 插件"),
    
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
    private static final Map<String, TemplateSourceTypeEnum> TEMPLATE_SOURCE_TYPE_CACHE = new ConcurrentHashMap<>();

    static {
        Arrays.stream(TemplateSourceTypeEnum.values()).forEach(item -> TEMPLATE_SOURCE_TYPE_CACHE.put(item.name(), item));
    }

    /**
     * 构造函数
     *
     * @param code    模版类型 Code
     * @param message 模版类型说明
     */
    TemplateSourceTypeEnum(Integer code, String message) {
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
        for (TemplateSourceTypeEnum type : TEMPLATE_SOURCE_TYPE_CACHE.values()) {
            if (type.getCode().equals(code)) {
                return type.name();
            }
        }
        throw new IllegalArgumentException("No enum constant " + TemplateSourceTypeEnum.class.getCanonicalName() + ". When Code is: " + code);
    }

    /**
     * 根据枚举名称获取枚举
     *
     * @param name 枚举名称
     *
     * @return 模版类型
     */
    public static TemplateSourceTypeEnum getEnumByName(String name) {
        if (TEMPLATE_SOURCE_TYPE_CACHE.containsKey(name)) {
            return TEMPLATE_SOURCE_TYPE_CACHE.get(name);
        }
        throw new IllegalArgumentException("No enum constant " + TemplateSourceTypeEnum.class.getCanonicalName() + "." + name);
    }
}
