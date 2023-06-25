package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 应用来源类型，表示应用的是从那个平台创建，或者下载的。比如 WrdPress ， Chrome插件等
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@SuppressWarnings("unused")
public enum AppSourceEnum implements IEnumable<Integer> {

    /**
     * Web 管理端
     */
    WEB(0, "Web 管理端"),

    /**
     * Web 应用市场
     */
    MARKET(1, "应用市场"),

    /**
     * Chrome 插件管理端
     */
    CHROME_PLUGIN_ADMIN(2, "Chrome 插件"),

    /**
     * Chrome 插件模版市场
     */
    CHROME_PLUGIN_MARKET(3, "Chrome 插件"),

    /**
     * Edge 插件
     */
    EDGE_PLUGIN_ADMIN(4, "Edge 插件"),

    /**
     * Edge 插件模版市场
     */
    EDGE_PLUGIN_MARKET(5, "Edge 插件"),

    /**
     * WordPress 插件
     */
    WORDPRESS_PLUGIN_ADMIN(6, "WordPress 插件"),

    /**
     * WordPress 插件模版市场
     */
    WORDPRESS_PLUGIN_MARKET(7, "WordPress 插件"),

    /**
     * 未知
     */
    UNKNOWN(8, "未知");

    /**
     * 应用类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 应用类型说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  应用类型 Code
     * @param label 应用类型说明
     */
    AppSourceEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
