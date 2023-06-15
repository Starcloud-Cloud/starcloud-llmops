package com.starcloud.ops.business.app.enums.app;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用来源类型，表示应用的是从那个平台创建，或者下载的。比如 WrdPress ， Chrome插件等
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@SuppressWarnings("unused")
public enum AppSourceEnum {

    /**
     * Web 管理端
     */
    WEB(0, "Web 管理端"),

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

    /**
     * WordPress 插件
     */
    WORDPRESS_PLUGIN(4, "WordPress 插件"),

    /**
     * 未知
     */
    UNKNOWN(5, "未知");

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
    private static final Map<String, AppSourceEnum> TEMPLATE_SOURCE_CACHE = new ConcurrentHashMap<>();

    static {
        Arrays.stream(AppSourceEnum.values()).forEach(item -> TEMPLATE_SOURCE_CACHE.put(item.name(), item));
    }

    /**
     * 构造函数
     *
     * @param code    应用类型 Code
     * @param message 应用类型说明
     */
    AppSourceEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 枚举缓存
     *
     * @return 枚举缓存
     */
    public static Map<String, AppSourceEnum> cache() {
        return TEMPLATE_SOURCE_CACHE;
    }
}
