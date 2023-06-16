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
